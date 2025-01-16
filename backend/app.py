from flask import Flask, request, jsonify
from utils.model_loader import load_sentiment_model
from utils.data_preprocessor import preprocess_text

# Initialize Flask app
app = Flask(__name__)

# Load the sentiment model
sentiment_pipeline = load_sentiment_model()


@app.route("/")
def home():
    return jsonify({"message": "Welcome to the Mental Health Companion AI API!"})


# Endpoint for sentiment analysis
@app.route("/analyze", methods=["POST"])
def analyze_sentiment():
    try:
        # Get the text input from the request
        data = request.json
        if "text" not in data or not data["text"]:
            return jsonify({"error": "Invalid input. 'text' field is required."}), 400

        text = data["text"]

        # Preprocess the input text
        preprocessed_text = preprocess_text(text)

        # Get sentiment predictions
        result = sentiment_pipeline(preprocessed_text)

        # Format the response
        response = {
            "input": text,
            "preprocessed_input": preprocessed_text,
            "predictions": result
        }
        return jsonify(response)

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
