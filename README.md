# announce

Control macOS's "announce the time" functionality from the command-line.

Requires [babashka].



```
Usage:

    ./announce.clj on
    ./announce.clj off
```

To check the stauts of the speech synthesis server, I do

```bash
launchctl print gui/$UID/com.apple.speech.synthesisserver | head -n4
```

[babashka]: https://babashka.org