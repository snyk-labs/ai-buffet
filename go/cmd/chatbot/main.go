// Simple chat loop using the Anthropic SDK.
// Model name is not passed directly to the client constructor, to showcase
// detection of model usage via code flow.
package main

import (
	"bufio"
	"context"
	"fmt"
	"os"
	"strings"

	"github.com/anthropics/anthropic-sdk-go"
)

const modelKey = "claude-sonnet-4-20250514"

func initClient() (anthropic.Client, anthropic.Model) {
	apiKey := os.Getenv("ANTHROPIC_API_KEY")
	if apiKey == "" {
		fmt.Fprintln(os.Stderr, "ANTHROPIC_API_KEY not set in environment.")
		os.Exit(1)
	}
	client := anthropic.NewClient()
	return client, anthropic.Model(modelKey)
}

func main() {
	client, model := initClient()

	fmt.Println("Chat with Claude (type 'exit' to quit).\n")

	scanner := bufio.NewScanner(os.Stdin)
	for {
		fmt.Print("> ")
		if !scanner.Scan() {
			break
		}
		prompt := scanner.Text()
		if prompt == "exit" {
			break
		}

		message, err := client.Messages.New(context.Background(), anthropic.MessageNewParams{
			Model:     model,
			MaxTokens: 1024,
			Messages: []anthropic.MessageParam{
				anthropic.NewUserMessage(anthropic.NewTextBlock(prompt)),
			},
		})
		if err != nil {
			fmt.Fprintln(os.Stderr, err)
			continue
		}

		var parts []string
		for _, block := range message.Content {
			if textBlock, ok := block.AsAny().(anthropic.TextBlock); ok {
				parts = append(parts, textBlock.Text)
			}
		}
		fmt.Println(strings.Join(parts, ""))
	}
}
