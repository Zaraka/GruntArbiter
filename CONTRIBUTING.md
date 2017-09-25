Contributing guide
-
First things first, let me say my thanks, Shadowrun community is sparse and I'm grateful to every one of you who decided to contribute.

### I have a bug and I want to report it
Ok! For bugs please use Github  issue system, but before you write them first please try to:

* Search for duplicates or recently closed issues similar nature.
* Make sure the bug is present even in the HEAD of master (unstable) branch. (If there's not nightly build available and you can't build your own then skip this step)

#### The issue creation 

Include:
* Platform
* App version
* Affected save files
* Steps to reproduce
* Expected behaviour
* Actual behaviour

### I have an idea for a new feature

Perfect, please use Github issue system as well and as above proceed with the first step. If such enhancement wasn't propoposed please go ahead and write it.

### I would like to contribute with art

In that case, I love you. Grunt Arbiter would nourish from new icons. They have to be in SVG format for easy resizing and in a single color.

#### I would like to contribute with non vector art

Still perfect, in that case please fill an Github issue with your proposal.

### I would like to contribute with code

Wonderful, Grunt Arbiter is written in latest Java 8 with the usage of Java FX. There isn't any Documentation or CodeStyle guidelines so please refer to several files about formatting, I try to be as much consistent as I can.

Preferably I would love if you solve existing issues, for implemeneting new features please contact me through Github Issue system.

###Before submitting pull request, make sure

* You can build and ran package.
* You did not have introduced new Maven warning during translation.
* You did not break older save files, in that case inform that in the pull request.
* All of the unit tests are passing.
