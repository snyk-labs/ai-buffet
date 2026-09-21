// Image generation using the OpenAI Images API (DALL-E).
package main

import (
	"context"
	"fmt"
	"os"
	"strings"

	openai "github.com/openai/openai-go"
)

const model = openai.ImageModelDallE3

func main() {
	apiKey := os.Getenv("OPENAI_API_KEY")
	if apiKey == "" {
		fmt.Fprintln(os.Stderr, "OPENAI_API_KEY is not set")
		os.Exit(1)
	}

	prompt := "A serene beach at sunset with gentle waves and a clear sky."
	if len(os.Args) > 1 {
		prompt = strings.Join(os.Args[1:], " ")
	}

	client := openai.NewClient()

	fmt.Println("Generating image...")
	resp, err := client.Images.Generate(
		context.Background(),
		openai.ImageGenerateParams{
			Model:          model,
			Prompt:         prompt,
			Size:           openai.ImageGenerateParamsSize1024x1024,
			N:              openai.Int(1),
			ResponseFormat: openai.ImageGenerateParamsResponseFormatURL,
			Quality:        openai.ImageGenerateParamsQualityStandard,
		},
	)
	if err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}

	if len(resp.Data) == 0 || resp.Data[0].URL == "" {
		fmt.Println("No image URL returned.")
		return
	}

	url := resp.Data[0].URL
	fmt.Println("Image URL:", url)

	outPath := "generated-image-url.txt"
	if err := os.WriteFile(outPath, []byte(url), 0o644); err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}
	fmt.Println("URL written to", outPath)
}
