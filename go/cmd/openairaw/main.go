// Calls the OpenAI Chat Completions API via raw HTTP (no SDK).
// Showcases model usage through direct HTTP.
package main

import (
	"bufio"
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"strings"
)

const (
	openAIAPIHost  = "https://api.openai.com"
	openAIChatPath = "/v1/chat/completions"
	openAIModel    = "gpt-4o-mini"
)

type chatRequest struct {
	Model       string    `json:"model"`
	Messages    []message `json:"messages"`
	Temperature float64   `json:"temperature"`
	MaxTokens   int       `json:"max_tokens"`
}

type message struct {
	Role    string `json:"role"`
	Content string `json:"content"`
}

type chatResponse struct {
	Choices []struct {
		Message struct {
			Content string `json:"content"`
		} `json:"message"`
	} `json:"choices"`
}

func getChatResponse(promptMessage string) (string, error) {
	apiKey := os.Getenv("OPENAI_API_KEY")
	if apiKey == "" || apiKey == "YOUR_OPENAI_API_KEY" {
		return "Error: Set OPENAI_API_KEY in the environment.", nil
	}

	body, err := json.Marshal(chatRequest{
		Model: openAIModel,
		Messages: []message{
			{Role: "user", Content: promptMessage},
		},
		Temperature: 0.7,
		MaxTokens:   150,
	})
	if err != nil {
		return "", err
	}

	req, err := http.NewRequest(http.MethodPost, openAIAPIHost+openAIChatPath, bytes.NewReader(body))
	if err != nil {
		return "", err
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+apiKey)

	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}

	if resp.StatusCode != http.StatusOK {
		return fmt.Sprintf("API Error: %d - %s. %s", resp.StatusCode, resp.Status, string(respBody)), nil
	}

	var data chatResponse
	if err := json.Unmarshal(respBody, &data); err != nil {
		return "", err
	}
	if len(data.Choices) == 0 {
		return "No response from chatbot.", nil
	}
	return data.Choices[0].Message.Content, nil
}

func main() {
	fmt.Println("OpenAI Chat (raw HTTP). Type 'exit' to quit.\n")

	scanner := bufio.NewScanner(os.Stdin)
	for {
		fmt.Print("You: ")
		if !scanner.Scan() {
			break
		}
		userInput := strings.TrimSpace(scanner.Text())
		if strings.EqualFold(userInput, "exit") {
			fmt.Println("Goodbye!")
			break
		}

		fmt.Println("Chatbot: Thinking...")
		reply, err := getChatResponse(userInput)
		if err != nil {
			fmt.Fprintln(os.Stderr, err)
			continue
		}
		fmt.Printf("Chatbot: %s\n\n", reply)
	}
}
