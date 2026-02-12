#!/bin/sh
pdflatex ./Assignment-1-LPSMT
bibtex ./Assignment-1-LPSMT
pdflatex ./Assignment-1-LPSMT
pdflatex ./Assignment-1-LPSMT

rm Assignment-1-LPSMT.aux Assignment-1-LPSMT.bbl Assignment-1-LPSMT.blg Assignment-1-LPSMT.log Assignment-1-LPSMT.toc Assignment-1-LPSMT.out
