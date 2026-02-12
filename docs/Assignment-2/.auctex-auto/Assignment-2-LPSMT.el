(TeX-add-style-hook
 "Assignment-2-LPSMT"
 (lambda ()
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "href")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperimage")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperbaseurl")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "nolinkurl")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "url")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "path")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "path")
   (TeX-run-style-hooks
    "latex2e"
    "report"
    "rep10"
    "eurosym"
    "hyperref"
    "biblatex"
    "graphicx")
   (TeX-add-symbols
    '("ignore" 1))
   (LaTeX-add-labels
    "sec:home"
    "sec:reader"
    "sec:hamburger"
    "sec:reading_list"
    "sec:info_reading")
   (LaTeX-add-bibliographies
    "refs"))
 :latex)

