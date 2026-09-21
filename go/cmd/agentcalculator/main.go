// LangChainGo agent with a calculator tool (NewOneShotAgent + Executor).
package main

import (
	"context"
	"fmt"
	"os"
	"strings"

	"github.com/tmc/langchaingo/agents"
	"github.com/tmc/langchaingo/chains"
	"github.com/tmc/langchaingo/llms/openai"
	"github.com/tmc/langchaingo/tools"
)

func main() {
	task := "Calculate the square root of 144 and then multiply the result by 5."
	if len(os.Args) > 1 {
		task = strings.Join(os.Args[1:], " ")
	}

	llm, err := openai.New(openai.WithModel("gpt-4o"))
	if err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}

	agentTools := []tools.Tool{tools.Calculator{}}
	agent := agents.NewOneShotAgent(llm, agentTools, agents.WithMaxIterations(3))
	executor := agents.NewExecutor(agent)

	fmt.Println("Task:", task)
	result, err := chains.Run(context.Background(), executor, task)
	if err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}

	fmt.Println("\nResult:", result)
}
