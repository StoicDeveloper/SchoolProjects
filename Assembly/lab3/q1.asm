        .orig   x3000
        
        ;my code here
        ;iterate through array, check evenness of each element
        ;then add element to appropriate variable
        
        lea     r1,array
        ld      r2,length   ;set length remaining
addloop 
        BRz     done
        and     r3,r1,#1    ;compare array element to 1
        BRz     addeven
addodd  
        lea     r4,sumOdd   ;put address of sumOdd into r4
        add     r5,r1,r4    ;add sumOdd with element place in r5
        str     r5,r4,#0    ;put sum into sumOdd
        BR      noteven

addeven 
        lea     r4,sumEven
        add     r5,r1,r4
        str     r5,r4,#0

noteven
        add     r1,r1,#1    ;increment array index
        add     r2,r2,#-1   ;decrement length remaining
        BR      addloop
        

done    
;        lea     r0,sumsMsg
;        puts
        ;lea     r2,sumOdd
        ;str     r2,r3,#0
        ;lea     r2,sumEven
        ;str     r2,r4,#0
        
        ;and     r3,r3,#0
        ld      r2,sumOdd
        ld      r3,sumEven
        lea     r0,eopMsg
        puts
        halt
        
eopMsg  .stringz    "\nProgrammed by Xian\nEnd of Processing.\n"
;sumsMsg .stringz    "\nSums of even and odd numbers:"
;andmsg  .stringz    " and "
sumOdd  .blkw   #1
sumEven .blkw   #1
length  .fill   #10
array   .fill   #15
        .fill   #10
        .fill   #6
        .fill   #-17
        .fill   #2
        .fill   #5
        .fill   #4
        .fill   #3
        .fill   #8
        .fill   #-8
        .end