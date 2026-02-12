#!/bin/sh

pdflatex Assignment-1-LPSMT -interaction=nonstopmode
biber Assignment-1-LPSMT
pdflatex Assignment-1-LPSMT -interaction=nonstopmode
pdflatex Assignment-1-LPSMT -interaction=nonstopmode
pandoc Assignment-1-LPSMT.tex --biblatex -s --toc -o ./Assignment-1-LPSMT.md

rm Assignment-1-LPSMT.aux Assignment-1-LPSMT.bbl Assignment-1-LPSMT.blg Assignment-1-LPSMT.log Assignment-1-LPSMT.toc Assignment-1-LPSMT.out