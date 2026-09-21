// Simple chat completion using the official OpenAI Go SDK.
// Showcases model usage via the openai-go package.
package main

import (
	"context"
	"fmt"
	"os"

	openai "github.com/openai/openai-go"
	"github.com/openai/openai-go/shared"
)

const model = shared.ChatModelGPT4oMini

func main() {
	apiKey := os.Getenv("OPENAI_API_KEY")
	if apiKey == "" {
		fmt.Fprintln(os.Stderr, "OPENAI_API_KEY is not set")
		os.Exit(1)
	}

	client := openai.NewClient()

	resp, err := client.Chat.Completions.New(
		context.Background(),
		openai.ChatCompletionNewParams{
			Model: model,
			Messages: []openai.ChatCompletionMessageParamUnion{
				openai.UserMessage("Say hello in one sentence."),
			},
			MaxTokens:   openai.Int(1024),
			Temperature: openai.Float(0.7),
		},
	)
	if err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}

	fmt.Println(resp.Choices[0].Message.Content)
}
