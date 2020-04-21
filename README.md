# Project Paraphrase (A.K.A Speak the docs)

Is a small project using googles Text-to-speech
to read aloud a [Read the Docs](https://readthedocs.org/) webpage in a more intuative way.
As I find it helpful to concentrate reading if I can hear it spoken
to me at the same time.

Recently I have been trying to do this with some webpage-to-speech apps.
However I have run into a number or problems with them:
* None have a sensible way to dictate code
* None have any visual indicator where the dictation is up to
* Some have a way to skip forwards and backwards
* None have the ability to tap to start at specific word

My current plan is to make a small app targeting [Read the Docs](https://readthedocs.org/) 
generated webpages (as the current documentation I want to read is on there). 
Using the built in Text-to-speech on android

* A more sensible parsing of code for Text-to-speech
* A preference to skip code blocks
* A visual indicator of current position
* Some media controls, forwards/backwards paragraph, forwards/backwards sentence, pause/play
* A way to select a word to start dictation from selected word