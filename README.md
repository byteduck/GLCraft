# IMPORTANT
I am not working on GLCraft actively anymore. (You may still see changes from time to time). If you would like to work on it, feel free to fork it!
On the infdev branch, you can see the plethora of code changes I made to attempt to add infinite worlds. However, this codebase is so bad that I gave up on it about a year ago and never came back to it. So, I've reverted the master branch to before the infinite world.

# GLCraft ![](https://raw.githubusercontent.com/byteduck/GLCraft/master/src/main/resources/textures/icons/icon32.png)
Just testing my skills to see if I can make a voxel game. I'm not planning to do anything with it.

Take a look at the wiki to see how you can make your own plugins for GLCraft.

# Launching
Make sure to use `-XX:MaxHeapFreeRatio=50 -XX:MinHeapFreeRatio=10` JVM arguments for the best memory usage if you actually care. GLCraft uses a ton of memory when generating / loading a world, and the JVM will keep most of the memory to itself when it's done being loaded. As a result, only like 10% of the heap ends up being used once the world is loaded. So, these arguments make sure the JVM downsizes the heap more aggressively. I *could* optimize the memory usage, but it's an old project and I don't see a reason to.

If you don't use those arguments, you'll end up seeing  usage in the 1-2GB range. With them, it's less than a GB.

# Todo

There are a lot of things I am planning to add to do with GLCraft.

Crossed out items means they are completed.

Italic means half completed.

* *Plugin API*
* ~~Better GUI System~~
 * ~~A GUI manager to make showing and hiding GUIs easier~~
 * ~~A GUIScreen class to make building guis easier~~
  * ~~Subclasses like GUIButton, GUITextField, GUIImage, etc~~
* ~~Lighting~~
 * ~~Day and night cycle~~
* *Weather*
* Infinite world
* ~~2x2 Crafting System~~
* ~~3x3 Crafting System~~
* ~~Re-add multiplayer~~
* User accounts
* *Animals/Monsters*
* *Sound*
* ~~Better menu~~
* *Options menu*
* ~~Tile entities~~
* *Better collision system to allow for custom collision hit boxes*
* ~~Make placing blocks actually work~~
* ~~Saving and Loading worlds~~
* Much, much more!


# License
    GLCraft - A simple voxel game using LWJGL.
    Copyright (C) 2015-2020 Byteduck
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
