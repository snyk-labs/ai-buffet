from datasets import Audio, load_dataset
from transformers import WhisperForConditionalGeneration, WhisperProcessor

# load model and processor
processor = WhisperProcessor.from_pretrained("sanchit-gandhi/whisper-medium-fleurs-lang-id")
model = WhisperForConditionalGeneration.from_pretrained(
    "sanchit-gandhi/whisper-medium-fleurs-lang-id"
)
forced_decoder_ids = processor.get_decoder_prompt_ids(language="french", task="translate")

# load streaming dataset and read first audio sample
ds = load_dataset("common_voice", "fr", split="test", streaming=True)
ds = ds.cast_column("audio", Audio(sampling_rate=16_000))
input_speech = next(iter(ds))["audio"]
input_features = processor(
    input_speech["array"],
    sampling_rate=input_speech["sampling_rate"],
    return_tensors="pt",
).input_features

# generate token ids
predicted_ids = model.generate(input_features, forced_decoder_ids=forced_decoder_ids)
# decode token ids to text
transcription = processor.batch_decode(predicted_ids, skip_special_tokens=True)
