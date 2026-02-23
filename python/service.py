import http.client
import json
import ssl
import os

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
OPENAI_API_HOST = "api.openai.com"
OPENAI_CHAT_COMPLETIONS_PATH = "/v1/chat/completions"
OPENAI_MODEL = "gpt-3.5-turbo"

def get_chat_response(prompt_message):
    """
    Sends a message to the OpenAI chat completions API and returns the response.
    """
    if not OPENAI_API_KEY or OPENAI_API_KEY == "YOUR_OPENAI_API_KEY":
        print("Error: Please replace 'YOUR_OPENAI_API_KEY' with your actual OpenAI API key.")
        return "API key not set."

    try:
        # Create an unverified SSL context to avoid certificate issues in some environments.
        # In a production environment, you should use a verified context.
        context = ssl._create_unverified_context()

        # Establish a secure HTTPS connection to the OpenAI API host
        conn = http.client.HTTPSConnection(OPENAI_API_HOST, context=context)

        # Define the headers for the API request
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {OPENAI_API_KEY}"
        }

        # Prepare the request body (payload) as a Python dictionary
        # The 'messages' array holds the conversation history.
        # Here, we only send the user's latest message.
        payload = {
            "model": OPENAI_MODEL,
            "messages": [
                {"role": "user", "content": prompt_message}
            ],
            "temperature": 0.7, # Controls randomness: higher values mean more random outputs
            "max_tokens": 150 # Maximum number of tokens to generate in the response
        }

        json_payload = json.dumps(payload)

        conn.request("POST", OPENAI_CHAT_COMPLETIONS_PATH, body=json_payload, headers=headers)

        response = conn.getresponse()
        response_data = response.read().decode("utf-8")
        conn.close()

        if response.status != 200:
            print(f"Error: HTTP Status {response.status} - {response.reason}")
            print(f"Response Body: {response_data}")
            return f"API Error: {response.status} - {response.reason}"

        response_json = json.loads(response_data)

        if "choices" in response_json and len(response_json["choices"]) > 0:
            chat_reply = response_json["choices"][0]["message"]["content"]
            return chat_reply
        else:
            return "No response from chatbot."

    except http.client.HTTPException as e:
        print(f"HTTP Error: {e}")
        return f"Network Error: {e}"
    except json.JSONDecodeError as e:
        print(f"JSON Decode Error: {e}")
        print(f"Raw response data: {response_data}")
        return f"Data Error: {e}"
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        return f"An unexpected error occurred: {e}"

def main():
    print("Welcome to the simple OpenAI Chatbot!")
    print("Type 'exit' to quit.")

    while True:
        user_input = input("You: ")
        if user_input.lower() == 'exit':
            print("Goodbye!")
            break

        print("Chatbot: Thinking...")
        chatbot_response = get_chat_response(user_input)
        print(f"Chatbot: {chatbot_response}")

if __name__ == "__main__":
    main()