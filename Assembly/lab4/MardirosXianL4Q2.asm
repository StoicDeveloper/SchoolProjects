; MardirosXianL4Q2.asm
;
; Course:       COMP2280
; Instructor:   Noman Mohammed
; Lab:          4
; Author:       Xian Mardiros
; Version:      2020/03/20
;
; Purpose:      Simulate a finite state machine
;
; Register Dictionary
; R0 characters for printing and prev input value
; R1 current S1
; R2 current S0
; R3 output value, calculations for determining next state
; R4 prev input character
; R5 curr input character
;
; Static Variables
; eopmsg - message to display at program termination
; ASCII  - the amount to add to cast from int to char
; /      - the value of the "/" character
; binary - used to check whether a value is 1 or 0
; space  - the value of the " " character

        .orig   x3000
        
        AND R1,R1,x0    ;set initial state to A
        AND R2,R2,x0

        GETC
        BR  input
        
loop    
        GETC            ;get input character
        ADD R5,R0,x0    ;move it to R5
        
        AND R3,R1,R2    ;print current output: Y = S1*S2
        LD  R0,ASCII
        ADD R0,R0,R3
        ;ADD R0,R3,x30
        PUTC
        
        LD  R0,space
        PUTC
        
        LD  R0,binary
        ADD R0,R0,R4
        ;ADD R0,R4,x-31  ;check if prev input is not 0 or 1
        BRp terminate
        ADD R0,R0,x1
        
        ;jump table
        ADD R3,R0,R1
        BRz A
        AND R3,R0,R1
        BRp D
        ADD R3,R2,x0
        BRz B
        BR  C
        
    A   AND R2,R2,x0
        BR  endtable
    B   AND R1,R1,x0
        AND R2,R2,x0
        ADD R2,R2,x1
        BR  endtable
    C   AND R1,R1,x0
        ADD R1,R1,x1
        AND R2,R2,x0
        BR  endtable
    D   AND R2,R2,x0
        ADD R2,R2,x1
    
    endtable

        
        ADD R0,R5,x0
    input
        PUTC
        
        ADD R4,R0,x0    ;move input character to R4
        
        ;AND R0,R0,x0    ;output "/"
        ;ADD R0,R0,x2F
        LD  R0,/
        PUTC
        BR  loop
        
terminate
        HALT
        
        
eopmsg  .stringz    "\nProgrammed by Xian Mardiros\nEnd of processing."
ASCII   .fill   x30
/       .fill   x2F
binary  .fill   x-31
space   .fill   x20
        .end