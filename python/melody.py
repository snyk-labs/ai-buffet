"""
The interesting part here is that we are not using AI explicitely, but
instead we are using a library that uses AI under the hood. We can flag
this by detecting the transitive dependency on the transformers library.
"""

import shutil
import sys

from beat_craft_sdk.core import BeatCraft, BeatCraftConfig
from beat_craft_sdk.utils.audio_converter import AudioConverter


def check_once(which: str) -> None:
    if shutil.which(which) is not None:
        return
    print(f"Dependency not found {which}. Try installing it with apt-get, brew etc.")
    exit(1)


def checks() -> None:
    check_once("ffmpeg")
    check_once("fluidsynth")


def init() -> BeatCraft:
    checks()
    sdk = BeatCraft()
    return sdk


def make_melody(sdk: BeatCraft, output_file: str) -> None:
    bcconfig = BeatCraftConfig(output_dir="output", file_name=output_file)
    sdk = BeatCraft(bcconfig)
    melodies = sdk.compose_melody()
    sdk.melody_to_midi(melodies)
    conv = AudioConverter(f"output/{output_file}.mid", f"output/{output_file}.wav")
    conv.midi_to_wav()
    sdk.generate_rythm(output_file)


def main() -> None:
    if len(sys.argv) != 2:
        print("Usage: python [this.py] [output-file-name]")
        exit(0)
    sdk = init()
    make_melody(sdk, sys.argv[1])


if __name__ == "__main__":
    main()
