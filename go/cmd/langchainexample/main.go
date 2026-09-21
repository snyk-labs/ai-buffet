// LangChainGo example with OpenAI LLM and a OneShot agent with tools.
package main

import (
	"context"
	"fmt"
	"os"

	"github.com/tmc/langchaingo/agents"
	"github.com/tmc/langchaingo/chains"
	"github.com/tmc/langchaingo/llms/openai"
	"github.com/tmc/langchaingo/tools"
)

type weatherTool struct{}

func (weatherTool) Name() string        { return "get_weather" }
func (weatherTool) Description() string { return "Get the weather for a given city" }
func (weatherTool) Call(_ context.Context, input string) (string, error) {
	return fmt.Sprintf("It's always sunny in %s!", input), nil
}

func main() {
	llm, err := openai.New(openai.WithModel("gpt-4o"))
	if err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}

	agentTools := []tools.Tool{weatherTool{}}
	agent := agents.NewOneShotAgent(llm, agentTools, agents.WithMaxIterations(3))
	executor := agents.NewExecutor(agent)

	result, err := chains.Run(context.Background(), executor, "What's the weather in Tokyo?")
	if err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}

	fmt.Println(result)
}
