;Keyboard interrupt lab
;COMP2280 Lab5
;Author: Xian Mardiros, 7862786
;Each time a key is pressed, print the integer represented by the 2 least significant bits of the character


.orig x3000

;main line
Main
	LD R6,STACKBASE		;set up user stack 

	LEA R0,KBHandler		
	LD R1,KBVEC
	STR R0,R1,#0		;set kb interrupt vector

	LD R0,KBEN		;enable keyboard interrupt
	STI R0,KBSR

Loop
	BR Loop			;loop forever

	HALT
	
;Keyboard interrupt handler (no need for Frame pointer)
KBHandler
	ADD R6,R6,#-1	;save R0
	STR R0,R6,#0
	ADD R6,R6,#-1	;save R1
	STR R1,R6,#0

	LDI R0,KBDR	;get key
	AND R0,R0,#3
	LD  R1,ASCII
	ADD R0,R0,R1

WaitToWrite
	LDI     R1,DSR	;get console output status
	BRzp    WaitToWrite

	STI R0,DDR	;write new character to console

	LDR R1,R6,#0	;restore the registers
	ADD R6,R6,#1

	LDR R0,R6,#0
	ADD R6,R6,#1

	RTI		;return from interrupt


STACKBASE   .FILL   x4000	;stack base (can be changed)
KBSR 	   	.FILL	xFE00	;keyboard status register
KBDR    	.FILL	xFE02	;keyboard data register 
DSR	        .FILL	xFE04	;console status register
DDR     	.FILL	xFE06	;console data register 

KBEN	    .FILL	x4000	;use to enable keyboard interrupt
KBVEC	    .FILL	x0180	;keyboard vector number/location

ASCII       .FILL   x30     ;the value to add to cast to a one-digit integer

	        .END
