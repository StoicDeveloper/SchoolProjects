#!/bin/python3
# c1 = '1111100101111001110011000001011110000110'
# c2 = '1111101001100111110111010000100110001000'
C1 = '11111001 01111001 11001100 00010111 10000110'
C2 = '11111010 01100111 11011101 00001001 10001000'

alpha = ascii('alpha')
bravo = ascii('bravo')
delta = ascii('delta')
gamma = ascii('gamma')
possibilityOneKey = []
possibilityTwoKey = []
letters = [alpha, bravo, delta, gamma]

def getBinary(word):
    output = ''
    for letter in word:
        output += format(ord(letter), '08b')
        output += ' ' 
    return output
def printBinary(word):
    print(getBinary(word))

print('alpha: ', end='')
printBinary('alpha')
print('bravo: ', end='')
printBinary('bravo')
print('delta: ', end='')
printBinary('delta')
print('gamma: ', end='')
print(C1)
print('c1   : ', end='')
print(C2)
print('c2   : ', end='')
printBinary('gamma')

def xor(string1, string2):
    output = ''
    for i in range(len(string1)):
        if string1[i] == ' ':
            output += ' '
        elif string1[i] == string2[i]:
            output += '0'
        else:
            output += '1'
    return output

print()
print('c1 xor alpha')
print('key  : ', end='')
print(xor(C1, getBinary('alpha')))

print()
print('c2 xor bravo')
print('key  : ', end='')
print(xor(C2, getBinary('bravo')))

print()
print('c1 xor delta')
print('key  : ', end='')
print(xor(C1, getBinary('delta')))

print()
print('c2 xor gamma')
print('key  : ', end='')
print(xor(C2, getBinary('gamma')))

def toHex(s):
    binBytes = s.split()
    hexBytes = []
    for b in binBytes:
        hexBytes.append(hex(int(b,2)))
    return " ".join(hexBytes)


print(toHex(xor(C2, getBinary('gamma'))))


    
