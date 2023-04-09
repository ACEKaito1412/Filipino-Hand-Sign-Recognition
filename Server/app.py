from flask import *
from PIL import Image
import json, time
import mediapipe as mp
import tensorflow as tf
import cv2
import numpy as np
import os
from io import BytesIO


app = Flask(__name__)


@app.after_request
def after_request(response):
    response.headers.add("Access-Control-Allow-Origin", "*")
    response.headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization")
    response.headers.add("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS")
    return response


#This endpoint is defined using the Flask web framework's @app.route() 
#decorator with the route URL "/" and the HTTP method "GET". 
#It maps the root URL of the web application to the home_page() function, 
# which is triggered when a user sends a GET request to the root URL of the application. 
# The home_page() function generates a JSON object data_set containing two key-value pairs: 
# "Result" with the value "STATUS ONLINE" and "Timestamp" with the value of the current 
# timestamp obtained from time.time() function. 
# The json.dumps() function is then used to convert the Python dictionary data_set into a 
# JSON string json_dump.
@app.route("/", methods=["GET"])
def home_page():
    data_set = {"Result": "STATUS ONLINE", "Timestamp": time.time()}
    json_dump = json.dumps(data_set)

    return render_template("index.html", json_dump=json_dump)

# This specific endpoint allows to get a user by passing the user name as a parameter,
# the endpoint returns a json object containing the message
# "Succesfully Loaded Requested Page: <user_name>",
# the timestamp of the request and the page name.
@app.route("/users/", methods=["GET"])
def request_page():
    user_request = str(request.args.get("user"))  # ?user=USER_NAME

    data_set = {
        "Result": f"Succesfully Loaded Requested Page: {user_request}",
        "Timestamp": time.time(),
    }
    json_dump = json.dumps(data_set)

    return json_dump


# This endpoint allows to send an image,
# the image is processed using a pre-trained model that detects hand landmarks
# and predict the letter of the alphabet the hand is forming.
# It returns the letter as the response.

# VARIABLES
MODEL_PATH = os.path.join("model", "abakada_1st.hdf5")
LABELS_PATH = os.path.join("model", "labels_abakada.txt")
LABELS_PATH2 = os.path.join("model", "labels_abakada2.txt")
MP_HANDS = mp.solutions.hands
HANDS = MP_HANDS.Hands(
    static_image_mode=False,
    max_num_hands=2,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5,
)

# This reads our labels.txt from our directory and load it to our class names array
CLASS_NAMES = []
with open(LABELS_PATH, "r") as file:
    for line in file:
        CLASS_NAMES.append(line.replace("\n", ""))

CLASS_NAMES2 = []
with open(LABELS_PATH2, "r") as file:
    for line in file:
        CLASS_NAMES2.append(line.replace("\n", ""))

MODEL_ABAKADA = tf.keras.models.load_model(MODEL_PATH)


@app.route("/load_img/", methods=["POST"])
def handle_image():
    # Get the image file from the request

    try:
        image_data = request.files["image"].read()

        pil_image = Image.open(BytesIO(image_data))

        image = np.array(pil_image)

        print(f"{image.shape[0]}, {image.shape[1]}, {image.shape[2]}")

        # image = cv2.imdecode(np.frombuffer(image_data, np.uint8), -1)
        results = HANDS.process(image)

        if results.multi_handedness is not None:
            data = []
            for res in results.multi_hand_landmarks:
                data = np.array([[rex.x, rex.y, rex.z] for rex in res.landmark])

            RES = MODEL_ABAKADA.predict(np.expand_dims(data, 0))

            print(f"{CLASS_NAMES[np.argmax(RES)]}")

            data_set = {
                "Result": f"{CLASS_NAMES[np.argmax(RES)]}",
                "Result_2": f"{CLASS_NAMES2[np.argmax(RES)]}",
                "Timestamp": time.time(),
                "Response": 200,
            }
            json_dump = json.dumps(data_set)

            return json_dump
        else:
            data_set = {
                "Result": "",
                "Result_2": "",
                "Timestamp": time.time(),
                "Response": 200,
            }
            json_dump = json.dumps(data_set)

            return json_dump

    except Exception as e:

        data_set = {"Result": f"{e}", "Timestamp": time.time()}
        json_dump = json.dumps(data_set)
        return json_dump


# VARIABLES
MODEL_PATH_ULAN_LAND = os.path.join("model", "ulan_1st.hdf5")
LABELS_PATH_ULAN = os.path.join("model", "labels_ulan.txt")
MP_HOLISTIC = mp.solutions.holistic
HOLISTIC_MODEL = MP_HOLISTIC.Holistic(
    min_detection_confidence=0.5, min_tracking_confidence=0.5
)

# This reads our labels.txt from our directory and load it to our class names array
CLASS_NAMES_ULAN = []
with open(LABELS_PATH_ULAN, "r") as file:
    for line in file:
        CLASS_NAMES_ULAN.append(line.replace("\n", ""))

MODEL_ULAN_LAND = tf.keras.models.load_model(MODEL_PATH_ULAN_LAND)


@app.route("/ulan_model/", methods=["POST"])
def handle_ulan():
    DATA_LIST = []
    try:
        images = request.files.getlist("image")

        for x, image in enumerate(images):
            image_r = image.read()
            pil_image = Image.open(BytesIO(image_r))

            # # Saving A FILE
            # file_path = "image/" + str(x) + ".jpg"
            # pil_image.save(file_path)

            image = np.array(pil_image)

            results = HOLISTIC_MODEL.process(image)

            pose = (
                np.array(
                    [
                        [res.x, res.y, res.z]
                        for res in results.pose_landmarks.landmark[0:16]
                    ]
                )
                if results.pose_landmarks
                else np.zeros([16, 3])
            )
            lh = (
                np.array(
                    [
                        [res.x, res.y, res.z]
                        for res in results.left_hand_landmarks.landmark
                    ]
                )
                if results.left_hand_landmarks
                else np.zeros([21, 3])
            )
            rh = (
                np.array(
                    [
                        [res.x, res.y, res.z]
                        for res in results.right_hand_landmarks.landmark
                    ]
                )
                if results.right_hand_landmarks
                else np.zeros([21, 3])
            )

            Landmark = np.concatenate([pose, lh, rh])

            DATA_LIST.append(Landmark)

        RES_1 = MODEL_ULAN_LAND.predict(np.expand_dims(DATA_LIST, 0))

        DATA_LIST.clear()

        data_set = {
            "Result": f"{CLASS_NAMES_ULAN[np.argmax(RES_1)]}",
            "Timestamp": time.time(),
        }

        json_dump = json.dumps(data_set)

        return json_dump

    except Exception as e:
        data_set = {"Result": f"{e}", "Timestamp": time.time()}
        json_dump = json.dumps(data_set)
        return json_dump


vid_count = []


@app.route("/image_collection/", methods=["POST"])
def image_collection():
    vid_count.append([1])

    action = "image/lilipad/"
    save_path = action + time.strftime("%Y-%m-%d_%H-%M-%S", time.gmtime()) + "/"
    if os.path.exists(action):
        print(f"Path Exist: {action} : Count {len(vid_count)}")
    else:
        os.mkdir(action)

    try:
        # directory_created
        os.mkdir(save_path)

        images = request.files.getlist("image")

        for x, image in enumerate(images):
            image_r = image.read()
            pil_image = Image.open(BytesIO(image_r))

            # Saving A FILE
            file_path = save_path + str(x) + ".jpg"
            pil_image.save(file_path)

        if len(vid_count) == 60:
            data_set = {
                "Result": f"Collected {len(vid_count)}",
                "Timestamp": time.time(),
            }

            vid_count.clear()

            json_dump = json.dumps(data_set)

            return json_dump
        else:
            data_set = {
                "Result": f"Collected {len(vid_count)}",
                "Timestamp": time.time(),
            }

            json_dump = json.dumps(data_set)

            return json_dump

    except Exception as e:
        data_set = {"Result": f"{e}", "Timestamp": time.time()}
        json_dump = json.dumps(data_set)
        return json_dump


if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True)


# flask run --host=0.0.0.0