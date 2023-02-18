        .orig   x3000
        

    
LOOP    lea     r0,mesg1
        PUTS
        GETC
        
        ;and     r1,r1,x0
        ;add     r1,r1,x04
        
        and     r2,r2,x0    ;has ctrl-D been pressed?
        LD      r2,ctrld
        add     r2,r0,r2
        BRz     END
        
        ;and     r2,r2,x2
        
        and     r3,r3,x0
        LD      r3,uppera
        add     r3,r0,r3
        BRnz    SKIP        ;is it less than A?
        and     r3,r3,x0
        LD      r3,upperz
        add     r3,r0,r3
        BRp     SKIP        ;is it more than Z?
        LD      r3,lower
        add     r0,r0,r3  ;change to lowercase
        
SKIP    PUTC
        and     r0,r0,x0
        add     r0,r0,xA
        OUT
        BR      LOOP
END     lea     r0,mesg2
        PUTS
        halt

        
mesg1   .stringz "Enter a character: "
mesg2   .stringz "Programmed by Xian\nEnd of Processing."
uppera  .fill   x-40
upperz  .fill   x-5A
lower   .fill   x20
ctrld   .fill   x-04
        .end