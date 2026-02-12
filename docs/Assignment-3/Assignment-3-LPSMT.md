---
author:
- |
  Blascovich Alessio\
  [`alessio.blascovich@studenti.unitn.it`](mailto:alessio.blascovich@studenti.unitn.it)
- |
  Tomassi Jan\
  [`jan.tomassi@studenti.unitn.it`](mailto:jan.tomassi@studenti.unitn.it)
bibliography:
- refs.bib
title: Manga-check
---

-   [Introduzione](#introduzione){#toc-introduzione}
    -   [Stack tecnologico](#stack-tecnologico){#toc-stack-tecnologico}
-   [Identificazione del segmento
    utente](#identificazione-del-segmento-utente){#toc-identificazione-del-segmento-utente}
-   [Stato dell'arte](#stato-dellarte){#toc-stato-dellarte}
-   [Wireframe e
    navigazione](#wireframe-e-navigazione){#toc-wireframe-e-navigazione}
    -   [Digressione Material
        Design](#digressione-material-design){#toc-digressione-material-design}
    -   [Library Home page](#sec:home){#toc-sec:home}
    -   [Comic List](#sec:comic_list){#toc-sec:comic_list}
    -   [File Manager](#file-manager){#toc-file-manager}
    -   [Add Chapter](#sec:add_chapter){#toc-sec:add_chapter}
    -   [Update Comic](#sec:update_comic){#toc-sec:update_comic}
    -   [Add Library](#sec:add_library){#toc-sec:add_library}
    -   [Reader](#sec:reader){#toc-sec:reader}
    -   [Search dialog](#search-dialog){#toc-search-dialog}
    -   [Menù ad hamburger](#sec:hamburger){#toc-sec:hamburger}
    -   [Reading list](#sec:reading_list){#toc-sec:reading_list}
    -   [Info reading](#sec:info_reading){#toc-sec:info_reading}
    -   [Add reading](#add-reading){#toc-add-reading}
    -   [Sign Up](#sign-up){#toc-sign-up}
-   [Architettura](#architettura){#toc-architettura}
    -   [DB remoto](#db-remoto){#toc-db-remoto}
    -   [File nello spazio
        privato](#file-nello-spazio-privato){#toc-file-nello-spazio-privato}
    -   [File nel resto del
        device](#file-nel-resto-del-device){#toc-file-nel-resto-del-device}
-   [Implementazione](#implementazione){#toc-implementazione}
    -   [Uso degli XML](#uso-degli-xml){#toc-uso-degli-xml}
    -   [Richieste API](#richieste-api){#toc-richieste-api}
    -   [Uso di Safe Args](#uso-di-safe-args){#toc-uso-di-safe-args}
    -   [Backup & Cache](#backup-cache){#toc-backup-cache}
    -   [Reader](#reader){#toc-reader}
-   [Valutazione](#valutazione){#toc-valutazione}
    -   [Review](#review){#toc-review}
-   [Analisi critica dei limiti
    dell'applicazione](#analisi-critica-dei-limiti-dellapplicazione){#toc-analisi-critica-dei-limiti-dellapplicazione}

# Introduzione

La maggior parte delle applicazioni/piattaforme di lettura si basa su un
modello ad abbonamento, un utente pagando una quota mensile può leggere
tutto il catalogo dei manga della piattaforma.\
Il problema di queste applicazioni è che vincola l'utente a dover pagare
un abbonamento per poter leggere manga che potrebbe già possedere in
formato virtuale.\
Le piattaforme/applicazioni per la catalogazione delle letture sono per
lo più gratis, ma permettono solo di gestire le proprie letture senza
alcuna forma di reader in app.\
La nostra idea è quella di colmare questa fascia di mercato,
**Manga-check** sarà questo il nome, dovrà permettere all'utente di
leggere qualsiasi fumetto lui possegga (formato *.cbz*) e tramite una
GUI dare la possibilità di catalogare le proprie letture.\
L'utente avrò anche la possibilità di eseguire un log-in per mantenere
la propria lista delle letture su più dispositivi.\
L'applicazione darà la possibilità di leggere sia manga che sono nella
memoria del dispositivo che si sta utilizzando, ma anche di leggere da
un server FTP remoto del quale l'utente possiede le credenziali.

## Stack tecnologico

-   Version control: git

-   Online repository: github

-   Editor: Android Studio

-   Language: Kotlin

-   Design style: Material

-   DBMS: SQLite

# Identificazione del segmento utente

In Europa, come nel resto del mondo, il mercato dei lettori di manga è
in costante aumento [@mangaOut].\
Pur ampliandosi a fasce d'età superiori, il mercato dei Manga rimane
molto legato ad un pubblico giovane che preferisce il formato digitale.\
Il formato digitale viene preferito per una serie di ragioni:

-   Costi inferiori rispetto alle coppie fisiche, un volume standard
    costa almeno €5

-   Tempi di attesa di per ricevere volume/capitolo

-   Reperibilità da un catalogo vastissimo

-   Facilita un primo approccio ad una nuova serie

Per una velocità della lettura e portabilità la maggior parte degli
utenti preferisce leggere i propri Manga su un dispositivo
mobile [@NLTreport].\
Per i motivi sopra citati la fascia d'età che potrebbe godere
maggiormente della nostra applicazione sono i ragazzi/ragazze tra i 12
ed i 18 anni ed i giovani adulti tra i 19 e 25 anni.

# Stato dell'arte

Le principali applicazioni per la lettura di manga sono 3 ma si basano
tutte su un abbonamento mensile e lettura solo online.

-   ## MangaToon

    con più di 10 milioni di download e una media di 3.8 stelle su 5.\
    L'applicazione è parzialmente gratuita ma manca di alcune
    traduzioni, una ritardo nella pubblicazione settimanale dei capitoli
    e nessuna possibilità di mantenere una lista delle letture.\
    Il catalogo è basato solo su Manga prodotti da case indipendenti.\
    L'app è monotematica per quanto riguarda la demografia delle
    letture, tutti i titoli sono Shojo [@shooManga].\
    Gli utenti lamentano troppe limitazioni nella versione gratuita e
    delle pubblicità troppo invasive.\
    [Pagina del Play
    Store](https://play.google.com/store/apps/details?id=mobi.mangatoon.comics.aphone.spanish).

-   ## MANGA Plus

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
    [Pagina dal Play
    Store](https://play.google.com/store/apps/details?id=jp.co.shueisha.mangaplus).

-   ## Crunchyroll Manga

    con più di 5 milioni di download e una media di 2.8 stelle su 5.\
    L'applicazione presenta un abbonamento gratis ed uno premium, con
    l'abbonamento gratis si ha accesso solo al primo capitolo di alcune
    opere il che inficia molto sull'esperienza dell'utente.\
    A dispetto degli screen sul play store molte opere di grande rilievo
    non sono presenti e si possono leggere solo Manga indipendenti.\
    La UI risulta molto facile da navigare e pulita, anche il reader è
    molto facile da usare e tiene traccia della pagina alla quale si è
    arrivati.\
    [Pagina dal Play
    Store](https://play.google.com/store/apps/details?id=com.crunchyroll.crmanga).

La nostra applicazione, come detto, si mette in contrapposizione a
questa corrente di mercato fornendo un prodotto gratis ma che si basa
sulla lettura offline.\
L'utente anziché pagare un abbonamento mensile potrebbe acquistare i
fumetti in formato digitale e leggerli sulla nostra applicazione.\
L'incentivo ad usare la nostra applicazione è dato dalla possibilità di
gestire le proprie letture in modo simile a come fa la piattaforma
online.\
All'utente viene quindi tolta la difficoltà di dover navigare attraverso
due applicazioni separate per reader e manager.

# Wireframe e navigazione

## Digressione Material Design

Le interfacce grafiche dell'applicazione sono state realizzate seguendo
il più possibile gli standard imposti da Material Design 3 [@matDes].\
In alcuni casi come nella ricerca dell'indice all'interno del
[Reader](#sec:raeder) abbiamo preferito divergere delle linee guida di
Material Design 3 per una UX migliore da parte dell'utente.

## Library Home page {#sec:home}

::: center
![image](library_home_page.png)
:::

L'applicazione si avvierà nella schermata contenente la libreria del
lettore.\
La libreria presenterà le opere possedute dall'utente ognuna
contrassegnata dal nome.\
Cliccando sull'anteprima di uno dei fumetti verrà aperta la [Comic
List](#sec:comic_list), mentre premendo sull'icona nell'angolo superiore
sinistro verrà aperto un [Menù ad hamburger](#sec:hamburger).\
Nell'angolo in basso a destra sarà presente un bottone per aggiungere
una nuova opera le cui informazioni verranno recuperate dal database
remoto che fa da supporto all'applicazione, le informazioni recuperate
saranno titolo, immagine di anteprima, breve descrizione, $\dots$\
La ricerca nel database verrà effettuata tramite il fragment [Add
Library](#sec:add_library)

## Comic List {#sec:comic_list}

::: center
![image](comic_list.png)
:::

In questa schermata l'utente troverà la lista dei volumi/capitoli, di
un'opera, che ha caricato all'interno dell'applicazione.\
Con il tasto "+" posizionato nell'angolo inferiore destro potrà
aggiungere un nuovo capitolo/volume associato all'opera, mentre tenendo
premuto sul nome di un elemento della lista potrà aggiornarne gli
attributi tramite il form presente in [Update
Comic](#sec:update_comic).\
Infine premendo su un volume/capitolo si verrà portati al
[Reader](#sec:reader).

## File Manager

::: center
![image](file_manager.png)
:::

L'applicazione dovrà interfacciarsi con il file manager per poter
permettere all'utente di selezionare i file *.cbz* da caricare.\
Una volta selezionato il file si verrà portati nel fragment [Add
chapter](#sec:add_chapter).

## Add Chapter {#sec:add_chapter}

::: center
![image](add_chapter.png)
:::

Tramite un breve form l'utente potrà modificare il nome del file e
indicare che capitolo sta inserendo.\
Il capitolo indicato dall'utente non andrà in alcun modo a modificare i
contenuti della [Reading List](#sec:reading_list) ma servirà solo a
mantenere una numerazione all'interno della vista [Comic
List](#sec:comic_list).

## Update Comic {#sec:update_comic}

::: center
![image](update_library.png)
:::

Tramite un dialog simile a [Add Chapter](#sec:add_chapter) l'utente
potrà andare a modificare i dati con cui è stato salvato il
capitolo/volume dell'opera selezionata.

## Add Library {#sec:add_library}

::: center
![image](add_library.png)
:::

Tramite una casella di testo l'utente potrà interrogare il database
remoto così d'aggiungere in locale una nuova opera con le relative
informazioni.

## Reader {#sec:reader}

::: center
![image](reader.png)
:::

La schermata del Reader conterrà una barra di navigazione nel lato
inferiore dello schermo.\
La barra di navigazione avrà dei comandi basici per muoversi all'interno
del file, un bottone per andare alla pagina precedente, uno per andare a
quella successiva e uno per ricercare la pagina con un determinato
numero.\
Il numero con cui si effettuerà la ricerca sarà assoluto e.g. la
copertina sarà la pagina numero 1.\
Nella parte superiore dello schermo sarà presente una freccia per poter
tornare alla schermata dell'opera e interrompere la lettura.

## Search dialog

::: center
![image](search_dialog.png)
:::

Una volta premuto sull'icona della ricerca verrà aperto un dialog dove
verrà chiesto all'utente di inserire l'indice della pagina alla quale si
vuole andare.\
Il formato di file scelto, ovvero *.cbz*, non contiene metadati per
l'enumerazione, quindi la numerazione inizia con la prima schermata che
avrà indice 1.\
Questa potrebbe essere una debolezza ma ogni altro lettore di *.cbz*
contiene questa imperfezione.

## Menù ad hamburger {#sec:hamburger}

::: center
![image](hamburger.png)
:::

Il Menù ad hamburger conterrà lo user name se l'utente è autenticato,
altrimenti lascerà uno spazio vuoto.\
Il resto del menù conterrà una lista con le seguenti voci:

-   **Reading list** che porterà l'utente alla propria lista delle
    letture.

-   **FTP server** permetterà all'utente di configurare il proprio
    server FTP dal quale dopo scaricare materiale.

-   **Log in/out** sarà una stringa adattiva, se l'utente non è
    autenticato presenterà la scritta "Log In" altrimenti la scritta
    "Log Out".

-   **Sign Up** permetterà all'utente di registrare un nuovo profilo,
    sarà presente solo se l'utente deve ancora effettuare l'accesso.

-   **Library** creerà una shortcut per poter tornare alla [Library Home
    page](#sec:home).

## Reading list {#sec:reading_list}

::: center
![image](reading_list.png)
:::

La reading list conterrà una lista delle letture che sono state inserite
dall'utente.\
Queste letture saranno divise in più categorie:

-   **Reading** conterrà le opere ch l'utente sta leggendo.\
    In questa lista affiancato al nome delle opere ci sarà un indicatore
    del capitolo al quale si è arrivati.

-   **Planning** quelle che pianificherà di leggere in futuro.

-   **Completed** le letture che l'utente ha concluso.

Premendo su un item della lista sarà possibile aggiornare il numero del
capitolo, i reading avranno capitoli maggiori uguali a 1, i planning
avranno capitolo attuale uguale a 0 mentre i completed avranno capitolo
uguale all'ultimo capitolo uscito.\
Per rimuovere una lettura basterà fare uno swipe e verrà rimossa
dall'elenco.\
Nella parte destra associto ad ogni lettura nella sezione **Reading**
sarà presente il numero del capitolo al quale il lettore è arrivato,
questo numero dovrà essere incrementato o diminuito dal lettore stesso,
la modifica avverrà tramite il dialog [Info reading](#sec:info_reading).

## Info reading {#sec:info_reading}

::: center
![image](info_reading.png)
:::

Da questa finestra di dialog sarà possibile incrementare o decrementare
il numero dei capitoli letti di un'opera selezionata.\
Sopra il selettore dei capitoli sarà presente il titolo dell'opera e una
breve descrizione di essa.\
L'utente potrà accedere a questa finestra di dialog premendo sul titolo
di un'opera.\

## Add reading

::: center
![image](add_reading.png)
:::

Tramite questa interfaccia l'utente potrà aggiungere una nuova opera
alla sua [Reading list](#sec:reading_list) in una categoria a scelta
tra:

-   **Planning**

-   **Reading**

-   **Completed**

Le opere che l'utente potrà selezionare saranno quelle presenti nel
database remoto al quale l'applicazione fa riferimento.

## Sign Up

::: center
![image](sign_up.png)
:::

Nel form per l'iscrizione al neo-utente verrà chiesto di inserire uno
user name che verrà poi mostrato a schermo (nel [Menù ad
hamburger](#sec:hamburger)), una mail con la quale fare la
autenticazione e una password che dovrà essere inserita due volte.\
Se uno dei parametri non rispetta ciò che il server si aspetta il campo
diventerà del colore assegnato agli errori e non si dovrà correggere
l'errore prima di proseguire.

# Architettura

::: center
![image](architettura.png)
:::

## DB remoto

Il database remoto è stato scritto in
[SQLite](https://www.sqlite.org/index.html) e viene gestito da un server
[Flask](https://flask.palletsprojects.com/en/2.3.x/), il contenuto del
Database è stato ottenuto sfruttando le API di
[AniList](https://anilist.gitbook.io/anilist-apiv2-docs/).\
Nel database abbiamo messo le principali informazioni di ogni manga: un
id, il titolo, descrizione, immagini di copertina, capitoli, volumi e
stato di pubblicazione.\
Per effettuare richieste alle API del server Flask abbiamo optato per
l'utilizzo della libreria [Ktor](https://ktor.io/) dato il grande di
team che la sviluppa essendo gestita da JetBrains.\
Le richieste al server sono state gestite in modo asincrono rispetto al
thread principale, e i loro risultati sono stati processati con delle
classi helper per formattare in modo corretto i dati ricevuti.\

## File nello spazio privato

L'applicazione crea dei file nel proprio spazio privato per poter
gestire tutti i dati dell'utente.\
I file più importanti sono due *xml* che fungono da elenco delle varie
library e reading che un utente possiede, per scrivere e leggere su
queste liste abbiamo usato delle coroutine per cercare di rendere
l'applicazione più responsive possibile.\
Per cercare di massimizzare l'efficienza delle richieste alle API del
server abbiamo deciso di fare il caching delle immagini di copertina dei
vari manga che vengono richiesti, questo ci permette di fare la
richiesta per un'immagine solo se nella cartella di cache facciamo una
miss del dato.\
Per la gestione dei capitoli abbiamo deciso di creare, per ogni library,
una cartella chiamata come l'id dell'opera stessa e di inserirci i
capitoli che un utente carica, la nomenclatura dei capitoli è la
seguente *\<numero_capitolo\>.cbz*.

::: center
``` {.xml style="xml" caption="Esempio del file readingList.xml"}
<?xml version="1.0" encoding="UTF-8"?>
<comics>
  <comic>
    <list>reading_list</list>
    <title>Berserk</title>
    <id>30002</id>
    <description>His name is Guts, the Black Swordsman, a feared warrior spoken of only in whispers. Bearer of giga...</description>
  </comic>
  <comic>
    <list>completed_list</list>
    <title>Monster</title>
    <id>30001</id>
    <description>Everyone faces uncertainty at some point in their lives. Even a brilliant surgeon like Kenzo Tenma i...</description>
  </comic>
</comics>
```

``` {.xml style="xml" caption="Esempio del file libraryList.xml"}
<?xml version="1.0" encoding="UTF-8"?>
<libraries>
  <library>
    <title>Gon</title>
    <id>31470</id>
  </library>
  <library>
    <title>Astro Boy</title>
    <id>30728</id>
  </library>
</libraries>
```
:::

## File nel resto del device

Manga-check avrà anche accesso allo storage esterno del device, questo
permetterà di poter importare/esportare la propria *reading list*, verrà
mostrato un messaggio Toast se la lista non esiste ancora e si trenta di
esportarla, mentre non verrà renderizzata la schermata se il file
importato dovesse essere malformato. I vari capitoli verranno importati
dallo storage estrerno all'app e copiati in quello privato come sopra
descritto.\
Sia per gli *xml* che per i *cbz* la selezione è stata forzata ai MIME
type [@rfc6838] dei rispettivi tipi di file.

# Implementazione

Manga-check è stata sviluppata seguendo un modello a singola Activity
con un [controllar di
navigazione](https://developer.android.com/guide/navigation) che funge
da istanziatore e sistema di passaggio di parametri da un fragment ad un
altro.\

## Uso degli XML

L'idea originale era di implementare un sistema di log in facoltativo
per gli tutti gli utenti che volevano mantenere sincronizzata la propria
reading list.\
Dopo una revisione con il docente abbiamo però optato per una soluzione
dall'implementazione più rapida, ovvero un importa/esporta manuale.\
Abbiamo deciso di adottare come standard dei file salvati in locale
*xml*, la scelta è stata fatta per la semplicità nella verifica manuale
dei dati, avendo fatto fatto largo uso della shell abd risultava molto
facile verificare se il file era stato scritto in modo corretto.\
La modificata e la gestione semplice dei file è stato possibile grazie
al package
[org.w3c.dom](https://kotlinlang.org/api/latest/jvm/stdlib/org.w3c.dom/),
un wrapper di javascript per la gestione di elementi del DOM, che ci ha
permesso di gestire ogni entry dei file *xml* come un nodo con al suo
interno degli attributi identificati dal nome dei campi.

::: center
![image](removeEntry_XML.png)
:::

## Richieste API

Le richieste API sono state gestite con il sopracitato package *ktor*,
una parte della formattazione delle rispose alle API è stata gestita
lato server per ridurre il codice da scrivere nell'applicazione e non
sprecare rallentare troppo l'app.\
Per gestire le risposte al meglio abbiamo di deciso di gestire lato
client come delle matrici di stringhe.\
Nel caso sottostante riceviamo i dati come una stringa che poi viene
separata grazie ad una regex ed in seguito inserita in una matrice
$[n][2]$ in cui $[n][0]$ contiene l'id del manga richiesto mentre
$[n][1]$ il nome.

::: center
![image](queryNames.png)
:::

## Uso di Safe Args

Nel progetto abbiamo dovuto trasferire alcuni dati tra due fragment,
come indicato nella documentazione Android abbiamo deciso di usare il
*navigation graph*, quindi vincolando i dati ad avere un determinato
tipo.\
Questo vincolo è stato possibile grazie all'utilizzo del plug in [Safe
Args](https://developer.android.com/guide/navigation/use-graph/pass-data#Safe-args)
che ci ha permesso di specificare delle *action* con un paylod di dati
tipizzati.\
Come descritto nelle linee guida non abbiamo passato in questi payload
strutture complesse, ma solo dati di tipi primitivi come *Int* e
*String*.

::: center
![image](action_navgraph.png)
:::

## Backup & Cache

Utilizzando le [funzionalità
native](https://developer.android.com/guide/topics/data/autobackup) di
Android abbiamo implementato un sistema di Backup che permette
all'utente di spostare la reading list senza bisogno di esportare il
file *xml*.\
Sfruttato la funzionalità di cache abbiamo salvato le immagini di
copertina dei comic in library e reding list, ed anche una versione
decompressa del file *cbz* da leggere.

## Reader {#reader}

I *cbz* vengono prima decompressi in cache, cosi da non occupare troppa
RAM, una volta fatto ciò, i file vengono converti durante l'esecuzione
in Bitmap ridimensionate per coprire più superficie possibile.\
Successivamente le Bitmap vengono associate ad una ImageView che si
occupa di mostrarle all'utente.\
Nel fragment del reader abbiamo implementato anche un bottone di ricerca
per navigare più agevolmente all'interno del comic e due bottoni per
muoversi tra le tavole.

# Valutazione

Abbiamo intervistato un tester della nostra applicazione e gli abbiamo
chiesto di redigere una breve recensione della nostra applicazione.\
Il tester non è stato scelto a caso, infatti è un appassionato di
lettura di fumetti, anche in formato digitale.

## Review

Ho trovato la fase di "selezione" dei fumetti leggermente confusionaria,
rispetto applicazioni più complesse che si limitano a scannerizzare
l'intero contenuto di un folder.\
Le tempistiche di apertura e caricamento dei documenti sono
relativamente rapide.\
Ho purtroppo trovato scomode le gesture: avrei preferito avanzare il
numero della pagina premendo sui lati del telefono, piuttosto che su un
soft-button dedicato (per intenderci, la freccia).\
Siccome è bloccata la rotazione del telefono, considero sgradevole il
fitting in larghezza delle splash-page, larghe il doppio rispetto le
singole tavole. Per questo motivo, risulta spesso difficile leggere i
balloon più piccoli.\
Avrei preferito fosse presente un disclaimer sull'occupazione di
memoria: l'applicazione infatti copia i documenti selezionati presso un
suo spazio dedicato in memoria. "Duplicare" anche solo 10 volumi può
richiedere un enorme spazio su massa.\
In un futuro aggiornamento, vorrei fosse introdotta la modalità di
lettura "endless", i.e. la possibilità di leggere i fumetti spostandosi
con "swipe" verso l'alto (in maniera non dissimile alle chat di un'app
di messaggistica).

# Analisi critica dei limiti dell'applicazione

Per quanto strutturata e testata l'applicazione presenta margini di
miglioramento.\
Primo tra tutti lo stile grafico, che non fornisce un'identità propria e
purtroppo non è ben integrabile con il nuovo paradigma di
colorazione [@matDesColor] basato sul wallpaper introdotto in Material
Design 3.\
Per questioni di tempo e semplicità non ci è stato possibile
implementare il log in dell'utente e abbiamo dovuto optare per una
soluzione semplificata anche se comunque efficiente.\
Il sistema di importing della reading list utilizzato, allo stato
attuale, è da intendersi solo come un sistema per spostare i dati da
un'installazione all'altra, infatti comporta una sovrascrittura completa
della precedente reading list.\
Neppure il database ed il sistema di query non è perfetto, può infatti
capitare che la descrizione di un qualche manga sia vuota e venga quindi
restituita la seguente stringa `...)];`.\
L'applicazione presenta anche alcuni margini di miglioramento:

-   L'implementazione del pintch to zoom nella sezione di reading del
    manga.

-   L'attivazione di un opt in per visualizzare anche fumetti per
    adulti.

-   L'ampliamento del database per il supporto anche ai comic
    occidentali.
