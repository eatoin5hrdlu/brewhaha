import pygame
import time
import pygame.midi

major=[0,4,7,12]
player = None
instrument = 0

def start():
    global player
    pygame.midi.init()
    player = pygame.midi.Output(2)
    player.set_instrument(48,1)
    player.note_on(64, 127)
    time.sleep(1)
    player.note_off(64, 127)

# display a list of MIDI devices connected to the computer
def devs():
    for i in range( pygame.midi.get_count() ):
        r = pygame.midi.get_device_info(i)
        (interf, name, input, output, opened) = r
        in_out = ""
        if input:
            in_out = "(input)"
        if output:
            in_out = "(output)"
        print ("%2i: interface: %s, name: %s, opened: %s %s" %
               (i, interf, name, opened, in_out))

def go(note):
    global player
    player.note_on(note, 127)
    time.sleep(1)
    player.note_on(note,0)

def arp(base,ints):
    for n in ints:
        go(base+n)

def chord(base, ints):
    global player
    player.note_on(base,127)
    time.sleep(0.05)
    player.note_on(base,0)
    player.note_on(base+ints[1],127)
    time.sleep(0.05)
    player.note_on(base+ints[1],0)
    player.note_on(base+ints[2],127)
    time.sleep(0.2)
    player.note_on(base+ints[2],0)
    player.note_on(base+ints[3],127)
    time.sleep(1)
    player.note_on(base+ints[3],0)

def playfile(file):
    global instrument
    datalist = eval(open(file,'r').read())
    cmd = 0
    note = 0
    d2 = 0
    d3 = 0
    player.set_instrument(instrument,0)
    print("instrument "+str(instrument))
    for (cmd, note, d2, d3) in datalist :
        if (cmd == 144 and d2 > 0):
            player.note_on(note,127,0)
            player.note_on(note,127,9)
            time.sleep(0.1)
        if (cmd == 128):
            player.note_off(note,0,0)
            player.note_off(note,0,9)
            time.sleep(0.1)
    player.note_off(note,127)
    instrument  = instrument + 1
    
def quit() :
    global player
    del player
    pygame.midi.quit()

def run() :
    start()
    while(True):
        playfile('tune.txt')
         
