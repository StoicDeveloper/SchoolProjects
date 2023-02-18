all: exfatreader

exfatreader: exfatreader.c
	clang exfatreader.c debug.c -g  -Wall -Wpedantic -Werror -Wextra -o exfatreader

clean:
	rm -f exfatreader

test:
	make
	./exfatreader a4image1.exfat info
	./exfatreader a4image2.exfat info
	#./exfatreader a4image1.exfat list
	#./exfatreader a4image2.exfat list
	./exfatreader a4image1.exfat get tree.txt
	./exfatreader a4image2.exfat get tree.txt
	./exfatreader a4image1.exfat get /dirs/in/dirs/in/dirs/matryoshka.jpg
	./exfatreader a4image2.exfat get /dirs/in/dirs/in/dirs/greetings.txt
	./exfatreader a4image1.exfat get /images/103052ab.jpg
	./exfatreader a4image2.exfat get /music/LICENSE
	./exfatreader a4image2.exfat get ebooks/mary-shelley_frankenstein.epub

