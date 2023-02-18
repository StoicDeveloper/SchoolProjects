; MardirosXianL4Q1.asm
;
; Course:       COMP2280
; Instructor:   Noman Mohammed
; Lab:          4
; Author:       Xian Mardiros
; Version:      2020/03/20
;
; Purpose:      Implement a recursive function
;
; Register Dictionary
; R0 hold temp values
; R1 hold x
; R5 frame pointer
; R6 stack pointer
;
; Static Variables
; result    - the output value
; x         - the input value
; stackbase - the address of the base of the stack
        .orig   x3000
Main
        LD  R6,stackbase
        LD  R1,x
        ADD R6,R6,x-1
        STR R1,R6,x0
        
        ADD R6,R6,x-1    ;set aside word for return value
        
        ADD R5,R6,x0
        
        JSR sum
        LDR R0,R5,x0
        ST  R0,result
        
        LDR R0,R6,x0
        
        ADD R6,R6,x2
        
        HALT
        
; activation record map
; R5+2 - return address
; R5+1 - subroutine parameter
; R5+0 - return value
; R1 - current x value
; R2 - hold x+x, and later the return value
sum
        ;store registers
        ADD R6,R6,x-1   ;save R5
        STR R5,R6,x0
        
        ADD R5,R6,x1    ;point R5 to return value
        
        ADD R6,R6,x-1   ;save R0
        STR R0,R6,x0
        
        ADD R6,R6,x-1   ;save R1
        STR R1,R6,x0
        
        ADD R6,R6,x-1    ;save R2
        STR R2,R6,x0
        
        ADD R6,R6,x-1   ;save R7
        STR R7,R6,x0
        
        LDR R1,R5,x1
        BRp recurse
        
    base
        STR R1,R5,x0
        BR  done
        
    recurse
        ADD R2,R1,x-1   ;compute new argument
        ADD R6,R6,x-1   ;store it on stack
        STR R2,R6,x0
        
        ADD R6,R6,x-1   ;set aside one word for return value
        
        JSR sum
        
        LDR R0,R6,x0
        ADD R6,R6,x2
        
        ADD R0,R1,R0
        
        STR R0,R5,x0
        
    done
        LDR R7,R6,x0    ;restore R7
        ADD R6,R6,x1
        
        LDR R2,R6,x0    ;restore R2
        ADD R6,R6,x1
        
        LDR R1,R6,x0    ;restore R1
        ADD R6,R6,x1
        
        LDR R0,R6,x0    ;restore R0
        ADD R6,R6,x1
        
        LDR R5,R6,x0    ;restore R5
        ADD R6,R6,x1
        
        RET
;end of subroutine

        
result      .fill   x0
x           .fill   #20
stackbase   .fill   xFE00
            .end