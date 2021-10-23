# QMP
Quick Media Player - A JavaFX based media launcher for live settings

## Introduction

The need for this came about when streaming church services during
lockdown. It turned out surprisingly difficult to drag a media
player into the right place on an extended desktop, maximise it to
fill the screen, and get it playing, in a professional looking way.

So that's what QMP does; it lets you build a list of media clips
you may want to play, and launch them with a double click at a
pre-defined location, scaled to a given size, in a border-less
window.

I've been developing on Windows, but being JavaFX-based, hopefully
it will work on other platforms, either out of the box, or with
potential to fix up. It was written to an urgent deadline, so is
untidy here and there, and there is low-hanging fruit for some
quick-fixes if anyone fancies it. (eg, Hacktoberfest!)

## Usage

* `run.bat` or `run.sh` to launch.

### Main screen

![Main Screenshot](img/screenshot1.jpg)

* You can manage playlists (new, load, save/as) from the File menu.
* Add movies by dragging them from another window onto the list,
or use the '+' button.
* Re-order movies (just for your convience) with the up/down
buttons. (`^` and `v`)
* Remove movies from the list with the `X` button.
* Play movies by doubling clicking them, or highlighting them
and clicking the play `>` button.
* The Play turns into pause `||` while the movie plays, and turns
into resume `>` if you pause the movie!
* To rewind the movie, press `<<`.
* The `O` button stops the movie and removes the window.

### Settings

![Main Screenshot](img/screenshot2.jpg)
![Main Screenshot](img/screenshot3.jpg)

* Settings -> Screen Settings is the only option so far.

* Fairly simple - you can manually set the location and
size of the video playind window (when it appears), or
you can autodetect monitors, and pick a full-screen one.

## Issues to fix:

See the issues list, or create new ones!

* The icons look terrible.
* Automatic check/install updates would be great, and fairly easy.
* There are issues with filenames containing quotes or special characters.
* There are issues with playing MOVs created with Apple devices, which
sometimes can be fixed by renaming them to MP4...
* There are also codec issues with more recent Apple devices, which can
be fixed with conversion through ffmpeg.
* An indication of movie playing time on the main interface would be nice.

## Known Issues I'm not sure how to fix.

* The interface on Windows supports dragging and dropping movies 
  into the playlist - however this *does not* work if you run as 
  administrator. Not sure exactly why you would, but perhaps this 
  happens accidentally. 
