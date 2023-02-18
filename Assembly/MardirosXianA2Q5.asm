        .orig   x3000
        lea     R0,HEAD
        LD      R1,Data1
        puts
        
        ;check if negative
        LD      R6,neg
        AND     R6,R6,R1    ;check most significant bit
        BRzp    startpos    ;if positive number, go to positive start
        AND     R6,R6,x0    
        ADD     R6,R6,x1    ;set R6 to one 
        LD      R7,pos      ;make most significant 1 to a zero (will need to add 1 to count later)
        AND     R1,R1,R7
        BR      startneg    ;go to the positive start
        
startpos
        AND     R6,R6,x0
startneg
        AND     R2,R2,x0    ;bit shift register
        AND     R3,R3,x0    ;even or odd register
        AND     R4,R4,x0    ;1 bit count register
loop
        ADD     R1,R1,x0    ;is it already 0?
        BRz     done
shift
        ADD     R1,R1,#-2   ;subtract 2 until a negative is reached
        BRn     endshift
        ADD     R2,R2,#1    ;effectively bit shifts R1 to R2
        BR      shift
endshift
        AND     R3,R1,#1    ;was R1 odd?
        ADD     R4,R4,R3    ;increment R4 if it was
        AND     R1,R1,x0    
        ADD     R1,R1,R2    ;place R2 into R1 and reset
        AND     R2,R2,x0
        BR      loop        ;go bitshift again, if needed
done
        ADD     R6,R6,x0    ;was the original number negative?
        BRz     notneg
        ADD     R4,R4,x1    ;then we lost a 1, better count it
notneg
        AND     R5,R5,x0    ;set R5 to 0
        ADD     R5,R4,#-10
        BRn     case1       ;was R4-10 negative? ie, 9 or less
        ADD     R5,R4,#-16  ;is it 15 or less
        BRn     case2
        BR      case3       ;it was 16
        
case1   ld      R0,number   ;9 or less
        ADD     R0,R4,R0
        BR      finish
case2   ld      R0,letter   ;10-15
        ADD     R0,R4,R0
        BR      finish
case3   ld      R0,one      ;16
        PUTC
        ld      R0,number
        
finish  PUTC
        HALT
        
HEAD    .stringz    "The number of 1s in Data1 is: "
Data1   .fill   xFACE
letter  .fill   x0037   ;the appropriate hex offsets
number  .fill   x0030
one     .fill   x0031   
neg     .fill   x8000   ;a one followed by zeroes
pos     .fill   x7FFF   ;a zero followed by ones
        .end