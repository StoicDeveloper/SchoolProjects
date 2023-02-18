; MardirosXianA3Q4.asm
;
; Course:       COMP2280
; Instructor:   Noman Mohammed
; Assignment:   3 Question 4
; Author:       Xian Mardiros
; Version:      2020/03/07
;
; Purpose:      Conduct linear search for element in int array using subroutine

; Register Dictionary:
; --------------------
; R0 - Various calculations, holding chars, addresses
; R1 - array length, after subroutine call used for calculations
; R2 - array address
; R3 - number of targets
; R4 - current target address
; R5 - frame pointer
; R6 - stack pointer
; R7 - return address
;
; Static Variables:
; -----------------
; ASCII         - Amount to add to a single-digit int to print its character
; FOUNDMSG      - Message for when the target is found
; NOTFOUNDMSG   - Message for when the target is not found
; EOPMSG        - Messgae to indicate program termination
; N             - Int array length
; SOURCE        - Int array
; NUMTARGETS    - Number of targets to search for
; TARGETS       - Array of targets
; STACK         - Location of run time stack

        .orig   x3000
        ;set registers
        ;LD  R1,N            
        LEA R2,SOURCE
        LD  R3,NUMTARGETS
        LEA R4,TARGETS
        LD  R6,STACK

        
    FIND
        LD  R1,N        ; needed extra register, used R1 so have to reset R1 to array length each loop

        ADD R6,R6,#-1   ;pass parameters
        STR R2,R6,#0    ;array address
        ADD R6,R6,#-1
        STR R1,R6,#0    ;length
        ADD R6,R6,#-1
        LDR R0,R4,#0
        STR R0,R6,#0    ;target value
        ADD R6,R6,#-1   ;address for return value

        ;LDR R5,R6,#0    ;set frame pointer ; this didn't work, it just took the value in the address pointed to by r6 add puts it in r5
        ADD R5,R6,#0    ;replaced from R6+1 due to incorrect offset
        ;ADD R5,R5,#1    
        
        JSR SEARCH       ;call subroutine SEARCH
        
        LDR R1,R5,#0    ; get return value
        ADD R6,R6,#-4
        ADD R0,R1,#1    ;print appropriate message
        BRz NOTFOUND
        LEA R0,FOUNDMSG
        PUTS
        LD  R0,ASCII
        ADD R0,R1,R0
        PUTC
        BR  FOUND
    NOTFOUND
        LEA R0,NOTFOUNDMSG
        PUTS
    FOUND
        ADD R3,R3,#-1   ;check if all targets have been searched for
        BRz DONE
        ADD R4,R4,#1    ;if not, then repeat subroutine
        BR  FIND
        
    DONE
        ; print EOP message
        LEA R0,EOPMSG
        PUTS
        HALT
        
     
     
; activation record map
; R5+0 - return value
; R5+1 - target value
; R5+2 - array length
; R5+3 - array address
; R0: current array element, and misc calculations
; R1: 2's complement of target value
; R2: current array index
; R3: current index address
; R4: array length
; R5: frame pointer
; R6: stack pointer
; R7: return address   

SEARCH
        ;save registers
        ADD R6,R6,#-1   
        STR R0,R6,#0
        ADD R6,R6,#-1   
        STR R1,R6,#0
        ADD R6,R6,#-1   
        STR R2,R6,#0
        ADD R6,R6,#-1   
        STR R3,R6,#0
        ADD R6,R6,#-1   
        STR R4,R6,#0
        
        ;setup
        LDR R1,R5,#1    ;take 2's complement of target value
        NOT R1,R1       
        ADD R1,R1,#1
        AND R2,R2,#0    ;set index to 0
        LDR R3,R5,#3    ;load first element address
        LDR R4,R5,#2    ;load array length
    
    LOOP
        ;find index of matching value
        LDR R0,R3,#0    ;load current array element
        ADD R0,R0,R1    ;compare current element and target
        BRz FOUNDIT
        ADD R2,R2,#1    ;increment index and index address
        ADD R3,R3,#1
        NOT R0,R2       ;take 2's complement of current index
        ADD R0,R0,#1
        ADD R0,R0,R4    ;check if index is beyond array length
        BRn NOTFOUNDIT
        BR  LOOP
        
    FOUNDIT
        STR R2,R5,#0
        BR  RESTORE
    NOTFOUNDIT
        STR R0,R5,#0
    RESTORE
        ;restore all registers
        LDR R4,R6,#0
        ADD R6,R6,#1 
        LDR R3,R6,#0
        ADD R6,R6,#1 
        LDR R2,R6,#0
        ADD R6,R6,#1 
        LDR R1,R6,#0
        ADD R6,R6,#1 
        LDR R0,R6,#0
        ADD R6,R6,#1 
        RET

ASCII       .fill   x30
FOUNDMSG    .stringz "\nFound at position: "
NOTFOUNDMSG .stringz "\nNot found."
EOPMSG      .stringz "\nProgrammed by Xian Mardiros\nEnd of Processing"
N           .fill   #10
SOURCE      .fill   #99
            .fill   #-33
            .fill   #57
            .fill   #0
            .fill   #29
            .fill   #-123
            .fill   #17
            .fill   #79
            .fill   #-1
            .fill   #22
NUMTARGETS  .fill   #4
TARGETS     .fill   #99  
            .fill   #-123
            .fill   #22
            .fill   #88
STACK       .fill   xFE00
            .end