Encode .txt file, output compressed .dat file in 9 bits
Decode binary string, output decompressed string as a file
Pre-optimization lzw-file3: 112ms
Post-optimization lzw-file3: 125ms
  -Wasn't really optimized, just changed to output to a file instead of printing
  -checks to see if file exists before running code
  -add timer
  -moved compress and decompression sequence into one method
  -added a couple comments
  -limited dictionary to 400 total entries
