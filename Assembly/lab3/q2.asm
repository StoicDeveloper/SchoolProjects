        .orig   x3000
        lea     r0,prompt
        puts
        
        ;my code here
        getc
        
        ld      r1,Y
        add     r1,r1,r0
        BRz     printyes
        ld      r1,N
        add     r1,r1,r0
        BRz     printno

        ldi     r0,neitherPtr
        BR      done
printyes
        ldi     r0,yesCharPtr
        BR      done
printno 
        ldi     r0,noCharPtr
        
done
        putc
        lea     r0,eopMsg
        puts
        halt
        
Y       .fill   xFFA7 ;2's compliment of 'Y'
N       .fill   xFFB2 ;2's compliment of 'N'
yesCharPtr  .fill   yesChar
noCharPtr   .fill   noChar
neitherPtr  .fill   neither
yesChar     .fill   x0031
noChar      .fill   x0030
neither     .fill   x002A
prompt      .stringz    "Enter a character.\n"
eopMsg      .stringz    "\nProgrammed by Xian\nEnd of processing.\n"
            .end