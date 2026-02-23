/**
 * Image generation using the OpenAI Images API (DALL-E).
 */

import OpenAI from "openai";
import { writeFileSync } from "fs";
import { join } from "path";

const MODEL = "dall-e-3";
const SIZE = "1024x1024" as const;

function getClient(): OpenAI {
  const apiKey = process.env.OPENAI_API_KEY;
  if (!apiKey) {
    throw new Error("OPENAI_API_KEY is not set");
  }
  return new OpenAI({ apiKey });
}

export async function generateImage(
  prompt: string,
  options?: { size?: "1024x1024" | "1792x1024" | "1024x1792"; n?: number }
): Promise<string[]> {
  const client = getClient();
  const response = await client.images.generate({
    model: MODEL,
    prompt,
    size: options?.size ?? SIZE,
    n: options?.n ?? 1,
    response_format: "url",
    quality: "standard",
  });

  const urls: string[] = [];
  for (const choice of response.data) {
    const url = choice.url;
    if (url) urls.push(url);
  }
  return urls;
}

async function main(): Promise<void> {
  const prompt =
    process.argv.slice(2).join(" ") ||
    "A serene beach at sunset with gentle waves and a clear sky.";

  try {
    console.log("Generating image...");
    const urls = await generateImage(prompt);
    if (urls.length === 0) {
      console.log("No image URL returned.");
      return;
    }
    console.log("Image URL:", urls[0]);

    const outPath = join(process.cwd(), "generated-image-url.txt");
    writeFileSync(outPath, urls[0], "utf-8");
    console.log("URL written to", outPath);
  } catch (err) {
    console.error(err);
    process.exit(1);
  }
}

main();
