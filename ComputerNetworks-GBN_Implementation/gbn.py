# Author: Xian Mardiros
# Purpose: Implement simulations of both versions of GBN
# Date: Nov 26, 2022
# Course: COMP4300 Computer Networks
# Assignment 2

# Both versions of GBN are implemented below.
# To use the modified version, pass in modified=True when constructing the GBN object

import random
import csv

MAX_SIZE = 8
MIN_SIZE = 1

class Frame():
    def __init__(self, count):
        self.num = count
        self.rnd = random.random()
    def isLost(self):
        return self.rnd <= 0.35
    def retransmit(self):
        self.rnd = random.random()


class GBN():
    def __init__(self, modified):
        if modified:
            csvfile = open('modifiedGBN.csv', 'w', newline='')
        else:
            csvfile = open('originalGBN.csv', 'w', newline='')
        self.modified = modified
        self.csv = csv.writer(csvfile, delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)
        self.windowSize = 4
        self.frames = [Frame(i) for i in range(self.windowSize)]
        self.totalSuccess = 0
        self.count = 4
        self.totalFails = 0

    def __str__(self):
        return f"Size: {self.windowSize}, Count: {self.count}"

    def ack(self, frame):
        print(f"Frame {frame.num} acked")
        if self.windowSize < MAX_SIZE and self.modified:
            self.windowSize += 1

    def loss(self, frame):
        print(f"Frame {frame.num} lost")
        if self.windowSize > MIN_SIZE and self.modified:
            self.windowSize = self.windowSize // 2
            if len(self.frames) > self.windowSize:
                del self.frames[self.windowSize:]

    def transmit(self):
        sent = []
        while len(self.frames) < self.windowSize:
            if len(self.frames) >= 1:
                newFrame = Frame(self.frames[len(self.frames)-1].num + 1)
            else:
                newFrame = Frame(self.totalSuccess)
            self.frames.append(newFrame)
            sent.append(newFrame.num)
            self.count += 1
        return sent

    def retransmit(self):
        # self.frames = []
        resent = []
        for frame in self.frames:
            frame.retransmit()
            resent.append(frame.num)
            self.count += 1
        return resent

    def run(self):
        round = 0
        if self.modified:
            roundFrames= open('modifiedGBNRounds.csv', 'w', newline='')
        else:
            roundFrames= open('originalGBNRounds.csv', 'w', newline='')
        roundFramesCSV = csv.writer(roundFrames, delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)
        while True:
            print(f"\nRound {round}")
            print(self)
            # curr = self.frames.pop(0)
            curr = self.frames[0]
            if curr.isLost():
                self.loss(curr)
                sent = self.retransmit()
                self.totalFails += 1
            else:
                self.ack(curr)
                self.frames.pop(0)
                self.totalSuccess += 1
                sent = self.transmit()
            roundFramesCSV.writerow([round, self.count])
            self.csv.writerow([round, self.windowSize])
            print(f"Sent frames {sent}")
            # if curr.num >= 500:
            if round >= 500:
                break
            round += 1
            # curr = self.frames[0]
        
if __name__ == '__main__':
    GBN(True).run()
    GBN(False).run()
