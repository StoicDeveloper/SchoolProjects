        .orig       x3000
        
        lea         r0,mesg1    ;put mesg1 in r0
        PUTS                    ;print it out
        GETC                    ;getc, placed in r0
        OUT                     ;print it out
        ;mov         r1,r0
        and         r1,r1,x0    ;clear r1
        add         r1,r1,r0    ;move first char to r1
        ;LD          r1,r0
        
        and         r0,r0,x0    ;new line
        add         r0,r0,xA
        OUT

        lea         r0,mesg1
        PUTS
    
        GETC
        OUT
        
        ;mov         r2,r0
        and         r2,r2,x0    ;clear r2
        add         r2,r2,r0    ;move first char to r2
        
        and         r0,r0,x0    ;new line
        add         r0,r0,xA
        OUT

        
        ;sub         r3,r1,r2
        and         r3,r3,x0    ;get 2's complement of r2
        not         r3,r2
        add         r3,r3,x1
        add         r3,r1,r3    ;then subtract
        
        BRz         ZERO
        lea         r0,mesg2
        PUTS
        BRn         NEG

        and         r0,r0,x0
        add         r0,r0,r2
        ;mov         r0,r2

        BR          DONE
NEG     ;mov         r0,r1
        and         r0,r0,x0
        add         r0,r0,r1
        BR          DONE
ZERO    lea         r0,mesg3
        PUTS
        BR          SKIP
        
DONE    PUTC
SKIP    and         r0,r0,x0
        add         r0,r0,xA
        PUTC
        lea         r0,mesg4
        PUTS
        halt
        
mesg1   .stringz "Enter any character: "
mesg2   .stringz "The smaller of the two characters is "
mesg3   .stringz "The two characters are equal."
mesg4   .stringz "Programmed by Xian\nEnd of processing."
        .end