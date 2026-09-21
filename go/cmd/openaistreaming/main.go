// Streaming chat completion using the OpenAI Go SDK.
// Demonstrates token-by-token streaming and model usage.
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

	prompt := "Count from 1 to 5, one number per line."
	if len(os.Args) > 1 {
		prompt = os.Args[1]
	}

	client := openai.NewClient()

	stream := client.Chat.Completions.NewStreaming(
		context.Background(),
		openai.ChatCompletionNewParams{
			Model: model,
			Messages: []openai.ChatCompletionMessageParamUnion{
				openai.UserMessage(prompt),
			},
			MaxTokens: openai.Int(1024),
		},
	)

	fmt.Print("Assistant: ")
	for stream.Next() {
		chunk := stream.Current()
		if len(chunk.Choices) == 0 {
			continue
		}
		delta := chunk.Choices[0].Delta.Content
		if delta != "" {
			fmt.Print(delta)
		}
	}
	fmt.Println()

	if err := stream.Err(); err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}
}
