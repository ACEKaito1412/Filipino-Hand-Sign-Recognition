# Filipino-Hand-Sign-Recognition

[Project description or tagline]

![Project Title](./images/project_title_image.jpg)

## Description

Filipino Hand Sign Recognition is a project that uses the Mediapipe library to recognize hand signs commonly used in Filipino Sign Language (FSL). The project is divided into three main folders:

- `training-model`: Contains the code and data for training the hand sign recognition model using machine learning techniques.
- `application`: Contains the code for the application that uses the trained model to recognize hand signs in real-time from a video feed.
- `server`: Contains the code for the server-side components of the application, such as API endpoints for processing hand sign recognition requests.

The project aims to provide a practical solution for real-time hand sign recognition in the context of FSL, which can be used for communication by individuals with hearing impairments in the Philippines.

![Project Description](./images/project_description_image.jpg)

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)
- [Credits](#credits)
- [Contact](#contact)
- [Appendix](#appendix)

## Installation

1. Clone the repository: `git clone https://github.com/ACEKaito1412/Filipino-Hand-Sign-Recognition.git`
2. Create a new virtual environment with Python 3.10.9: `python3.10 -m venv myenv` (replace `myenv` with your preferred environment name)
3. Activate the virtual environment:

   - For Windows: `myenv\Scripts\activate`
   - For macOS/Linux: `source myenv/bin/activate`

4. Install the required dependencies: `pip install -r requirements.txt`, which includes:
   - mediapipe
   - opencv
   - tensorflow
   - pyngrok
   - fastdtw

### Data Collection and Training

1. Run Jupyter Notebook: `jupyter notebook`
2. In Jupyter Notebook, open `collect_data.ipynb` and collect necessary data, such as actions, by collecting images for training.
3. After collecting data, open `process_data.ipynb` and run the data processing steps using Mediapipe to extract keypoints from the hand images and save them as NumPy files.
4. Once data processing is complete, go to the training section and train the hand sign recognition model, making any necessary changes if you have modified anything in the code.

### Application

To run the Android application, you will need the following dependencies:

- `androidx.appcompat:appcompat:1.5.1`
- `com.google.android.material:material:1.7.0`
- `androidx.constraintlayout:constraintlayout:2.1.3`
- `org.tensorflow:tensorflow-lite-support:0.1.0`
- `org.tensorflow:tensorflow-lite-metadata:0.1.0`
- `org.tensorflow:tensorflow-lite-gpu:2.3.0`
- `implementation project(path: ':opencv')`
- `junit:junit:4.13.2` (for testing)
- `androidx.test.ext:junit:1.1.4` (for testing)
- `androidx.test.espresso:espresso-core:3.5.0` (for testing)
- `com.squareup.okhttp3:okhttp-bom:4.10.0` (BOM for OkHttp)
- `com.squareup.okhttp3:okhttp` (OkHttp without version)
- `com.squareup.okhttp3:logging-interceptor` (OkHttp logging interceptor)

Make sure to include these dependencies in your Android project's build.gradle file or other relevant configuration files, and update the versions as necessary based on your project's requirements.

## Server-side Setup

1. Go to ngrok.com and create an account/login to have unlimited time for using ngrok.
2. Once logged in, download and install ngrok on your server.
3. Copy the trained model file from the training directory to the server.
   - If you have made any changes to the model, make sure to rename the file accordingly.
4. Check the labels used in the trained model and update them in the server-side code, if needed.
5. Run Flask and ngrok on your server using the following steps:
   - Start Flask server: `python app.py` (or the name of your Flask app file)
   - Start ngrok: `ngrok http 5000` (assuming Flask is running on port 5000)
6. ngrok will generate a public URL, which you can use to access your Flask app remotely.

## Contributing

Contributions to the Filipino Hand Sign Recognition project are welcome! If you would like to contribute, please follow these steps:

1. Fork the repository.
2. Create a new branch for your contribution: git checkout -b my-contribution
3. Make your changes and commit them: git commit -m "Description of my contribution"
4. Push your changes to your forked repository: git push origin my-contribution
5. Create a pull request from your forked repository to the main repository.

## License

The software is released under the MIT License.

MIT License

Copyright (c) 2023 ACEKaito1412

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## Credits

Special thanks to the following individuals or organizations for their contributions to this project:

- [Angelo Orejenes](https://www.facebook.com/otsuka24) for their assistance with data collection and preprocessing and for their valuable input during code review and testing.
- [Shekinah Mae](https://www.facebook.com/popeyeeeeeeeeee) for their assistance with data collection and preprocessing and for their valuable input during code review and testing.
- [Jerome Rasalan](https://www.facebook.com/jerome.rasalan15) for their assistance with data collection and preprocessing and for their valuable input during code review and testing.
- [Johanes Encarnacion](https://www.facebook.com/johanes.encarnacion) for their assistance with data collection and preprocessing and for their valuable input during code review and testing.

- [Melody Lim Gabas](https://www.facebook.com/teamgabasxD) for their guidance and support throughout the development process.

We would also like to express our gratitude to the authors of the following open-source libraries used in this project:

- [Mediapipe](https://github.com/google/mediapipe) for hand pose estimation.
- [OpenCV](https://opencv.org/) for image processing.
- [TensorFlow](https://www.tensorflow.org/) for machine learning.
- [Pyngrok](https://pyngrok.readthedocs.io/en/latest/) for exposing the local Flask server to the internet.
- [fastdtw](https://pypi.org/project/fastdtw/) for dynamic time warping distance computation.

Without the contributions of these individuals and the availability of these open-source libraries, this project would not have been possible.

## Contact

For any questions or inquiries about the Filipino Hand Sign Recognition project, please contact ['macdon.jc.bscs@gmail.com'].

## Appendix

Here are some additional resources related to the Filipino Hand Sign Recognition project:

- [GitHub repository for `mediapipe_multi_hands_tracking_aar_example`](https://github.com/jiuqiant/mediapipe_multi_hands_tracking_aar_example)
- [YouTube video tutorial on hand tracking with MediaPipe](https://www.youtube.com/watch?v=a99p_fAr6e4&t=304s&ab_channel=IvanGoncharov)
- [YouTube video tutorial on hand sign recognition using TensorFlow and MediaPipe](https://www.youtube.com/watch?v=vQZ4IvB07ec&t=15s&ab_channel=NicholasRenotte)
- [YouTube video tutorial on hand gesture recognition with MediaPipe](https://www.youtube.com/watch?v=doDUihpj6ro&t=261s&ab_channel=NicholasRenotte)
- [GitHub repository for `hand-gesture-recognition-mediapipe`](https://github.com/kinivi/hand-gesture-recognition-mediapipe)
- [GitHub repository for `hand-gesture-recognition-using-mediapipe`](https://github.com/Kazuhito00/hand-gesture-recognition-using-mediapipe)
- ['Filipino Sign Language Recognition using Deep Learning'](https://dl.acm.org/doi/abs/10.1145/3485768.3485783)

You can refer to these resources for further information, examples, and inspiration for your own hand sign recognition project.
