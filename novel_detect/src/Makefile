all: novel_detect
novel_detect: main.o Array.o QuadProg++.o uQuadProg++.o
	g++ main.o Array.o QuadProg++.o uQuadProg++.o -o novel_detect
main.o: main.cc
	g++ -c main.cc
QuadProg++.o: QuadProg++.cc
	g++ -c QuadProg++.cc
uQuadProg++.o: uQuadProg++.cc
	g++ -c uQuadProg++.cc
clean:
	rm -rf *.o novel_detect
