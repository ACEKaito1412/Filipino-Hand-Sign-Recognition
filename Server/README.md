# FHS API - Endpoint Information

This repository contains the Flask-based API for the Filipino Hand Sign Recognition (FHS) project. The API provides various endpoints for interacting with the FHS model, which is trained to recognize hand signs in images.

## Features

- Predicts hand signs in images using a pre-trained model
- Provides endpoints for sending image data and receiving recognized hand signs as responses
- Includes image collection for training data from a phone
- Easy and convenient way to integrate FHS functionality into other applications or services

## Installation

1. Clone the repository to your local machine:
   - git clone https://github.com/yourusername/fhs-api.git
2. Install the required dependencies: `pip install -r requirements.txt`, which includes:

   - mediapipe
   - opencv
   - tensorflow
   - pyngrok
   - fastdtw

3. Run the Flask application:
   - python app.py
   - The API will be running locally at `http://localhost:5000/`.

## Endpoints

### /

- URL: `/`
- HTTP method: GET
- Description: This endpoint maps the root URL of the web application to the `home_page()` function, which generates a JSON object `data_set` containing information about the status of the API. The JSON object is returned as a response to the user.

### /load_img/

- URL: `/load_img/`
- HTTP method: POST
- Description: This endpoint allows you to send an image file as a POST request to the API, which is processed using a pre-trained model to detect hand landmarks and predict the letter of the alphabet the hand is forming. The predicted hand sign is returned as a JSON response.

### /ulan_model/

- URL: `/ulan_model/`
- HTTP method: POST
- Description: This endpoint allows you to send image data for processing using a model called `ULAN_MODEL`. The processed data is returned as a JSON response.

## Usage

1. Make sure the Flask application is running locally by running `python app.py` in your terminal.

2. Send a GET request to `http://localhost:5000/` to check the status of the API.

3. Send a POST request to `http://localhost:5000/load_img/` with an image file as the payload to predict the hand sign in the image.

4. Send a POST request to `http://localhost:5000/ulan_model/` with image data as the payload to process the data using the `ULAN_MODEL`.

5. Receive the JSON response from the API with the predicted hand sign or processed data.

## Contributing

If you'd like to contribute to this project, please open an issue or submit a pull request. Contributions are welcome!

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for more information.
