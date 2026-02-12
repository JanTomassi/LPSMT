---
author:
- |
  Blascovich Alessio\
  `alessio.blascovich@studenti.unitn.it`
- |
  Tomassi Jan\
  `jan.tomassi@studenti.unitn.it`
bibliography:
- refs.bib
title: 'Manga-check'
---

-   [Introduzione](#introduzione)
    -   [Stack tecnologico](#stack-tecnologico)
-   [Identificazione del segmento
    utente](#identificazione-del-segmento-utente)
-   [Stato dell'arte](#stato-dellarte)
-   [Wireframe e navigazione](#wireframe-e-navigazione)
    -   [Digressione Material Design](#digressione-material-design)
    -   [Library - Home page](#sec:home)
    -   [Reader](#sec:reader)
    -   [Search dialog](#search-dialog)
    -   [Menù ad hamburger](#sec:hamburger)
    -   [Reading list](#reading-list)
    -   [Info reading](#sec:info_reading)
    -   [Add reading](#add-reading)
    -   [Sign Up](#sign-up)

Introduzione
============

La maggior parte delle applicazioni/piattaforme di lettura si basa su un
modello ad abbonamento, un utente pagando una quota mensile può leggere
tutto il catalogo dei manga della piattaforma.

Il problema di queste applicazioni è che vincola l'utente a dover pagare
un abbonamento per poter leggere manga che potrebbe già possedere in
formato virtuale.

Le piattaforme/applicazioni per la catalogazione delle letture sono per
lo più gratis, ma permettono solo di gestire le proprie letture senza
alcuna forma di reader in app.

La nostra idea è quella di colmare questa fascia di mercato,
**Manga-check** sarà questo il nome, dovrà permettere all'utente di
leggere qualsiasi manga lui possegga (formato *.cbz*) e tramite una GUI
dare la possibilità di catalogare le proprie letture.

L'utente avrò anche la possibilità di eseguire un log-in per mantenere
la propria lista delle letture su più dispositivi.

L'applicazione darà la possibilità di leggere sia manga che sono nella
memoria del dispositivo che si sta utilizzando, ma anche di leggere da
un server ftp remoto del quale l'utente possiede le credenziali.

Stack tecnologico
-----------------

-   Version control: git

-   Online repository: github

-   Editor: Android Studio

-   Language: Kotlin

-   Design style: Material

-   DBMS: sqllite o PostgresSQL

Identificazione del segmento utente
===================================

In Europa, come nel resto del mondo, il mercato dei lettori di manga è
in costante aumento [@mangaOut].

Pur ampliandosi a fasce d'età superiori, il mercato dei Manga rimane
molto legato ad un pubblico giovane che preferisce il formato digitale.

Il formato digitale viene preferito per una serie di ragioni:

-   Costi inferiori rispetto alle coppie fisiche, un volume standard
    costa almeno €5

-   Tempi di attesa di per ricevere volume/capitolo

-   Reperibilità da un catalogo vastissimo

-   Facilita un primo approccio ad una nuova serie

Per una velocità della lettura e portabilità la maggior parte degli
utenti preferisce leggere i propri Manga su un dispositivo
mobile [@NLTreport]

Per i motivi sopra citati la fascia d'età che potrebbe godere
maggiormente della nostra applicazione sono i ragazzi/rgazze tra i 12 ed
i 18 anni ed i giovani adulti tra i 19 e 25 anni.

Stato dell'arte
===============

Le principali applicazioni per la lettura di manga sono 3 ma si basano
tutte su un abbonamento mensile e lettura solo online.

-   MangaToon
    ---------

    con più di 10 milioni di download e una media di 3.8 stelle su 5.\
    L'applicazione è parzialmente gratuita ma manca di alcune
    traduzioni, una ritardo nella pubblicazione settimanale dei capitoli
    e nessuna possibilità di mantenere una lista delle letture.\
    Il catalogo è basato solo su Manga prodotti da case indipendenti.\
    L'app è monotematica per quanto riguarda la demografia delle
    letture, tutti i titoli sono Shojo [@shooManga].\
    Gli utenti lamentano troppe limitazioni nella versione gratuita e
    delle pubblicità troppo invasive.\
    Link per la pagina del Play Store.

-   MANGA Plus
    ----------

    con più di 10 milioni di download e una media di 4.1 stelle su 5.\
    L'app parte da un abbonamento gratis che permette di leggere una
    parte molto ridotta del catalogo.\
    Essendo un'applicazione di origine orientale la UI risulta molto
    diversa dai canoni occidentali, con elementi che distraggono
    l'occhio e immagini molto grandi e piene di elementi.\
    Apparentemente non è possibile effettuare un log in per
    sincronizzare tra i vari dispositivi l'elenco delle letture.\
    Presenta anche una sezione dove gli autori più piccoli possono
    pubblicare i loro lavori in modo facilitato.\
    Link per la pagina dal Play Store.

-   Crunchyroll Manga
    -----------------

    con più di 5 milioni di download e una media di 2.8 stelle su 5.\
    L'applicazione presenta un abbonamento gratis ed uno premium, con
    l'abbonamento gratis si ha accesso solo al primo capitolo di alcune
    opere il che inficia molto sull'esperienza dell'utente.\
    A dispetto degli screen sul play store molte opere di grande rilievo
    non sono presenti e si possono leggere solo Manga indipendenti.\
    La UI risulta molto facile da navigare e pulita, anche il reader è
    molto facile da usare e tiene traccia della pagina alla quale si è
    arrivati.\
    Link per la pagina del Play Store.

La nostra applicazione, come detto, si mette in contrapposizione a
questa corrente di mercato fornendo un prodotto gratis ma che si basa
sulla lettura offline.\
L'utente anziché pagare un abbonamento mensile potrebbe acquistare i
Manga in formato digitale e leggerli sulla nostra applicazione.\
L'incentivo ad usare la nostra applicazione è dato dalla possibilità di
gestire le proprie letture in modo simile a come fa la piattaforma
online.

All'utente viene quindi tolta la difficoltà di dover navigare attraverso
due applicazioni separate per reader e manager.

Wireframe e navigazione
=======================

Digressione Material Design
---------------------------

Le interfacce grafiche dell'applicazione sono state realizzate seguendo
il meglio possibile gli standard imposti da Material Design
3 [@matDes].\
In alcuni casi come nella ricerca dell'indice all'interno del abbiamo
optato per un infrazione delle linee guida di Material Design 3 per
migliorare l'usabilità da parte dell'utente.

Library - Home page {#sec:home}
-------------------

![image](library_home_page.png)

L'applicazione si avvierà nella schermata contenente la libreria del
lettore.\
La libreria presenterà gli eventuali fumetti posseduti dall'utente
ognuno contrassegnato da nome file e che presenta come anteprima
copertina o in alternativa la prima pagina.\
Cliccando sull'anteprima di uno dei fumetti verrà aperto il , mentre
premendo sull'icona nell'angolo superiore sinistro verrà aperto un .\
Nell'angolo in basso a destra sarà presente un bottone per aggiungere
una nuova opera le cui informazioni verrnno recuperate dal database
remoto che fa fa da supporto all'applicazione.\
Se invece si vuole aggiungere un nuovo capitolo/volume bisognerà entrare
nel'opera di appartenenze e selezionare dai file locali o dal server FTP
il file che vogliamo inserire.

Reader {#sec:reader}
------

![image](reader.png)

La schermata del Reader deve contenere una barra di navigazione nel lato
inferiore dello schermo.\
La barra di navigazione dovrà avere dei comandi basici per moversi
all'interno del file, un bottone per andare alla pagina precedente, uno
per andare a quella successiva e uno per ricercare la pagina con un
determinato numero.\
Il numero con cui si effettuerà la ricerca sarà assoluto e.g. la
copertina sarà la pagina numero 1.\
Nella parte superiore dello schermo sarà presente una freccia per poter
tornare alla e interrompere la lettura.

Search dialog
-------------

![image](search_dialog.png)

Una volta premuto sull'icona della ricerca vierrà aperto un dialog dove
verrà chiesto all'utente di inserire l'indice della pagina alla quale si
vuole andare.

Menù ad hamburger {#sec:hamburger}
-----------------

![image](hamburger.png)

Il Menù ad hamburger conterrà lo user name se l'utente ha effettuato il
log in, altrimenti lascerà uno spazio vuoto.\
Il resto del menù conterrà una lista con le seguenti voci:

-   **Reading list** che porterà l'utente alla propria lista delle
    letture.

-   **FTP server** permetterà all'utente di configurare il proprio
    server FTP.

-   **Log in/out** sarà una stringa adattiva, se l'utente non è loggato
    presenterà la scritta \"Log In\" altrimenti la scritta \"Log Out\".

-   **Sign Up** permetterà all'utente di registrare un nuovo profilo,
    sarà presente solo se l'utente deve ancora effettuare l'accesso.

Reading list
------------

![image](reading_list.png)

La reading list conterrà una lista delle letture che sono state inserite
dall'utente.\
Queste letture saranno divise in più categorie:

-   **Reading** conterrà le opere ch l'utente sta leggendo.\
    In questa lista affiancato al nome dell'opere ci sarà un indicatore
    per modificare il capitolo al quale si è arrivati.

-   **Planning** quelle che pianificherà di leggere in futuro.

-   **Completed** le letture che l'utente ha concluso.

Premendo su un item della lista sarà possibile aggiornare il numero del
capitolo, i reading avranno capitoli maggiori uguali a 1, i planninig
avranno capitolo attuale uguale a 0 mentre i completed avranno capitolo
uguale all'ultimo capitolo uscito.\
Per rimuovere una lettura basterà fare uno swipe e verrà rimossa
dall'elenco.\
Nella parte destra associto ad ogni lettura nella sezione **Reading**
sarà presente il numero del capitolo al quale il lettore è arrivato,
questo numero dovrà essere incrementato o diminuito dal lettore stesso,
la modifica avverrà tramite il dialog .

Info reading {#sec:info_reading}
------------

![image](info_reading.png)

Da questa finestra di dialog sarrà possibile incrementare o decrementare
il numero dei capitoli letti di un'opera selezionata.

Add reading
-----------

![image](add_reading.png)

Tramite questa interfaccia l'utente potrà aggiungere una nuova opera
alla sua reading list in una categoria a scelta tra:

-   **Planning**

-   **Reading**

-   **Completed**

Le opere che l'utente potrà selezionare saranno quelle presenti nel
database al quale l'applicazione fa riferimento.

Sign Up
-------

![image](sign_up.png)

Nel form per l'iscrizione al neo-utente verrà chiesto di inserire uno
user name che verrà poi mostrato a schermo, una mail con la quale fare
il log in e una password che dovrà essere inserita due volte.\
Se uno dei parametri non rispetta ciò che il server aspetta il campo
diventerà del colore assegnato agli errori.
