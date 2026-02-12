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
    -   [Mock-up](#mock-up){#toc-mock-up}
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
    -   [Redesign](#redesign){#toc-redesign}
        -   [Library Home
            page](#sec:home_redesign){#toc-sec:home_redesign}
        -   [Chapter
            list](#sec:ch_list_redesign){#toc-sec:ch_list_redesign}
        -   [Add chapter](#add-chapter){#toc-add-chapter}
        -   [Add
            series](#sec:add_series_redesign){#toc-sec:add_series_redesign}
        -   [Tracker](#tracker){#toc-tracker}
        -   [Reader](#reader){#toc-reader}
-   [Architettura](#architettura){#toc-architettura}
    -   [Room DB](#room-db){#toc-room-db}
    -   [Files .cbz](#files-.cbz){#toc-files-.cbz}
    -   [AniList API](#anilist-api){#toc-anilist-api}
-   [Implementazione](#implementazione){#toc-implementazione}
    -   [Divisione in
        moduli](#divisione-in-moduli){#toc-divisione-in-moduli}
    -   [Struttura del DB](#struttura-del-db){#toc-struttura-del-db}
    -   [Richieste API](#richieste-api){#toc-richieste-api}
    -   [Uso di Safe Args](#uso-di-safe-args){#toc-uso-di-safe-args}
    -   [Backup Del DB](#backup-del-db){#toc-backup-del-db}
    -   [Reader](#reader-1){#toc-reader-1}
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
[Reader](#sec:reader) abbiamo preferito divergere delle linee guida di
Material Design 3 per una UX migliore da parte dell'utente.

## Mock-up

Test sono una prova

### Library Home page {#sec:home}

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

### Comic List {#sec:comic_list}

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

### File Manager

::: center
![image](file_manager.png)
:::

L'applicazione dovrà interfacciarsi con il file manager per poter
permettere all'utente di selezionare i file *.cbz* da caricare.\
Una volta selezionato il file si verrà portati nel fragment [Add
chapter](#sec:add_chapter).

### Add Chapter {#sec:add_chapter}

::: center
![image](add_chapter.png)
:::

Tramite un breve form l'utente potrà modificare il nome del file e
indicare che capitolo sta inserendo.\
Il capitolo indicato dall'utente non andrà in alcun modo a modificare i
contenuti della [Reading List](#sec:reading_list) ma servirà solo a
mantenere una numerazione all'interno della vista [Comic
List](#sec:comic_list).

### Update Comic {#sec:update_comic}

::: center
![image](update_library.png)
:::

Tramite un dialog simile a [Add Chapter](#sec:add_chapter) l'utente
potrà andare a modificare i dati con cui è stato salvato il
capitolo/volume dell'opera selezionata.

### Add Library {#sec:add_library}

::: center
![image](add_library.png)
:::

Tramite una casella di testo l'utente potrà interrogare il database
remoto così d'aggiungere in locale una nuova opera con le relative
informazioni.

### Reader {#sec:reader}

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

### Search dialog

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

### Menù ad hamburger {#sec:hamburger}

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

### Reading list {#sec:reading_list}

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
Nella parte destra associato ad ogni lettura nella sezione **Reading**
sarà presente il numero del capitolo al quale il lettore è arrivato,
questo numero dovrà essere incrementato o diminuito dal lettore stesso,
la modifica avverrà tramite il dialog [Info reading](#sec:info_reading).

### Info reading {#sec:info_reading}

::: center
![image](info_reading.png)
:::

Da questa finestra di dialog sarà possibile incrementare o decrementare
il numero dei capitoli letti di un'opera selezionata.\
Sopra il selettore dei capitoli sarà presente il titolo dell'opera e una
breve descrizione di essa.\
L'utente potrà accedere a questa finestra di dialog premendo sul titolo
di un'opera.\

### Add reading

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

### Sign Up

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

## Redesign

Dopo la prima presentazione abbiamo optato per un design più semplice e
pulito che cercasse di spiegare all'utente cosa sta facendo.\
Su consiglio dei docenti abbiamo modificato il flow dell'applicazione,
le maggiori criticità della precedente versione dell'applicazione erano
nella parte di UX.\
Abbiamo pertanto deciso di scrivere nel report sia il precedente
design/flow sia quello nuovo, per quanto riguarda le altre sezioni del
report abbiamo optato per riportare solo l'ultima versione.\
Abbiamo inoltre apportato delle modifiche alla nomenclatura usata
nell'applicazione:

-   **Series**: sono gli oggetti che prima venivano Comic

-   **Tracker**: il fragment che precedentemente era inteso come
    [Reading list](#sec:reading_list).

### Library Home page {#sec:home_redesign}

::: center
![image](library_redesign.jpg)
:::

La home page non ha subito molte variazioni.\
Abbiamo però rimosso il menù ad hamburger in favore di una barra di
stato nel alto inferiore dello schermo, e il pulsante di aggiunta di una
serie specifica meglio cosa l'utente andrà ad aggiungere, il pulsante
inserisce le series in stato `READING`.\
Le uniche serie delle quali verrà fatto il display sono quelle che sono
state messe nello stato `READING` nel tracker.

### Chapter list {#sec:ch_list_redesign}

::: center
![image](chapter_list_redesign.jpg)
:::

Ora la UI presenta delle isole più grandi per ogni capitolo di
un'opera.\
Le isole contengono tutte le informazioni, sulla base delle quali,
l'utente potrebbe cercare un capitolo specifico, immagine di copertina
nome e numero.\
Nella parte inferiore delle isole c'è una barra di progressione che sta
ad indicare la percentuale di lettura.\
Come presente anche nella [home page](#sec:home_redesign) è presente un
bottone per l'aggiunta di una nuova risorsa, in questo caso di un
capitolo.

### Add chapter {#add-chapter}

::: center
![image](add_chapter_redesign.jpg)
:::

Dalla [lista dei capitoli](#sec:ch_list_redesign), premendo il pulsante
di aggiunta, si verrà portati a questo form.\
Verranno chieste all'utente solo le informazioni fondamentali come il
file, il titolo da dare e il numero del capitolo.\
All'utente prima della conferma apparirà un dialog indicante che le
risorse non verranno portate nello spazio privato dell'applicazione,
quindi se i file dovessero essere spostai non sarebbero più accessibili.

### Add series {#sec:add_series_redesign}

::: center
![image](add_series_redesign.jpg)
:::

Abbiamo optato per una semplice barra di ricerca dalla quale
visualizzare tutti i risultati inerenti alla stringa cercata.\
Dopo aver premuto sul risultato cercato si aprirà un form per la
conferma dei dati, i dati saranno presi dalle API di Anilist ma sarà
comunque possibile per l'utente modificare i dati premendo nella voce in
alto a destra.

### Tracker

::: center
![image](tracker_redesign.jpg)
:::

Come suggerito dai docenti abbiamo mantenuto un'unica gesture per la
selezione, sarà quindi possibile tenere premuto una voce per
selezionarla.\
Abbiamo mantenuto un bottone di aggiunta che riporta ad un form analogo
a quello del [add series](#sec:add_series_redesign).\
Le series che vengono aggiunte alla [libreria](#sec:home_redesign)
vengono automaticamente aggiunte a questa sezione con lo stato di
`READING`.\
Inoltre le voci presenti nella sezione `READING` sono clickabili e
riportano all'ultimo capitolo aperto di quella serie, se non è presente
tale capitolo si viene riportati alla lista dei capitoli di tale opera.\
In caso di modifica verrà aperto un dialog tramite il quale l'utente
potrà modificare lo stato di lettura di un'opera.\
Nel caso la voce venisse eliminata dal tracker questa sarà eliminata
anche dalla [homer page](#sec:home_redesign).

### Reader {#reader}

::: center
![image](reader_redesign.jpg)
:::

Premendo su un capitolo l'utente verrà portato al reader, l'utente si
potrà muovere tra le pagine con degli swipe a sinistra e destra.\
Il Reader è stato implementato in modo tale che riprenda dall'ultima
pagina che l'utente ha visualizzato. Premendo sull'immagine appariranno
una top bar e una bottom bar, nella bottom bar l'utente potrà trascinare
il dito per scorrere velocemente tra le pagine.

# Architettura

::: center
![image](architettura2.png)
:::

## Room DB

L'applicazione crea un database
[SQLite](https://www.sqlite.org/index.html) in locale, la comunicazione
con esso avverrà attraverso
[Room](https://developer.android.com/reference/androidx/room/package-summary).\
Il DB sarà composto da 2 tabelle:

1.  **Chapters**: contenente tutti i capitoli aggiunti all'applicazione,
    sono memorizzati tutti i dati fondamentali per risalire al file e
    all'opera di appartenenza.\

2.  **Series**: è l'insieme di tutte le serie che sono state aggiunte
    all'applicazione, vi sono memorizzate anche informazioni ottenute
    dalle API di
    [AniList](https://anilist.gitbook.io/anilist-apiv2-docs/).

## Files .cbz

Attraverso un `Intent` l'applicazione può aprire il file explorer di
default e tramite quello selezionare un file che rispetta il MIME
type [@rfc6838] `application/x-cbz`.\
Il file non verrà spostato nello spazio privato per evitare sprechi di
memoria, verrà quindi salvato solo L'URI del file selezionato.

## AniList API

É stata rimossa qualsiasi forma di intermediazione tra applicazione e le
API di AniList.\
Per interfacciarsi con il server abbiamo utilizzato il client GraphQL
[Apollo](https://www.apollographql.com/docs/kotlin) che ci ha permesso
di creare query direttamente dal dispositivo.\
L'eliminazione di un layer intermedio ci ha permesso di gestire al
meglio le risorse che ottenevamo, come le immagini grazie a
[Glide](https://bumptech.github.io/glide/).

# Implementazione

Manga-check è stata sviluppata seguendo un modello a singola Activity
con un [controller di
navigazione](https://developer.android.com/guide/navigation) che funge
da istanziatore e sistema di passaggio di parametri da un fragment ad un
altro.\

## Divisione in moduli

## Struttura del DB

``` {.Kotlin language="Kotlin" caption="Parte di Series.kt"}
@Entity(indices = [Index(value = ["title"], unique = true)])
data class Series(@ColumnInfo("title") val title: String,
    @ColumnInfo("status") val status: ReadingState,
    @ColumnInfo("is one device") val isOnDevice: Boolean,
    @ColumnInfo("description") val description: String?,
    @ColumnInfo("chapters") val chapters: Int?,
    @ColumnInfo("image url") val imageUri: Uri?,
    @ColumnInfo("is one-shot") val isOne_shot: Boolean,
    @ColumnInfo("lastAccess") val lastAccess: ZonedDateTime,
    @ColumnInfo("last chapter read") val lastChapterRead: Int,
    @PrimaryKey(autoGenerate = true) val uid: Long = 0
)
```

``` {.Kotlin language="Kotlin" caption="Parte di Chapter.kt"}
@Entity(foreignKeys = [ForeignKey(entity = Series::class,
    parentColumns = ["uid"],
    childColumns = ["seriesId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE)],
    indices = [Index(value = ["seriesId", "chapter_num", "state"], unique = true)])
data class Chapter(@ColumnInfo("seriesId") val seriesId: Long,
    @ColumnInfo("chapter_title") val chapter: String,
    @ColumnInfo("chapter_num") val chapterNum: Int,
    @ColumnInfo("pages") val pages: Int,
    @ColumnInfo("current_page") val currentPage: Int,
    @ColumnInfo("state") val state: ReadingState,
    @ColumnInfo("comic_file") val file: Uri?,
    @ColumnInfo("lastAccess") val lastAccess: ZonedDateTime,
    @PrimaryKey(autoGenerate = true) val uid: Long = 0)
```

Nella definizione di Chapter abbiamo usato l'attributo `unique` per
poter poi verificare la presenza di numeri duplicati e quindi fermare
l'utente dall'inserimento.

## Richieste API

Le richieste API sono state gestite con il sopracitato package *Apollo*,
per garantire una fruibilità maggiore tutte le richieste vengono gestite
in un Thread separato rieptto a quello della UI.\
Per ricevere i dati abbiamo usato dei
[LiveData](https://developer.android.com/reference/kotlin/androidx/lifecycle/LiveData),
quindi una volta che i dati sono effettivamente presenti vengono
mostrati a UI, le immagini vengono gestite grazie a *Glide*.\
Le entry così generate vengono inserite in una RecyclerView con la quale
l'utente andrà ad interagire.

``` {.Kotlin language="Kotlin" caption="Parte del codice usato per fare le Query"}
 /**
 * RecyclerView that manage the query result as a list of entry with only the
 * name
 */
class QueryAdapter
(private val model
 : SeriesSearchModel, private val resultAction
 : () -> Unit)
    : RecyclerView.Adapter<QueryAdapter.ViewHolder> ()
{
  private var dataSet = List<SearchByNameQuery.Medium ?> (0){ null }

  data class ViewHolder
  (val view
   : SeriesSearchSelectorBinding)
      : RecyclerView.ViewHolder (view.root)

            fun updateData (newData
                            : List<SearchByNameQuery.Medium
                                   ?>){ val prevSize = dataSet.size val newSize
                                        = newData.size dataSet
                                        = newData notifyItemRangeChanged (
                                            0, max (prevSize, newSize)) }

        override fun onCreateViewHolder (parent
                                         : ViewGroup, viewType
                                         : Int)
      : ViewHolder{ val view
                    = SeriesSearchSelectorBinding.inflate (LayoutInflater.from (
                        parent.context)) return ViewHolder (view) }

        override fun
        getItemCount ()
      : Int{ return dataSet.size }

        override fun onBindViewHolder (holder
                                       : ViewHolder, position
                                       : Int)
  {
    val view = holder.view

            val englishTitle = dataSet[position]?.title?.english
            val romajiTitle = dataSet[position]?.title?.romaji
            val nativeTitle = dataSet[position]?.title?.native

            val title = englishTitle ?: romajiTitle ?: nativeTitle
            val description = dataSet[position]?.description
            val chapters = dataSet[position]?.chapters
            val imageUrl = dataSet[position]?.coverImage?.large

            view.containerMangaName.text = title

            Glide.with(view.root).load(imageUrl).circleCrop().into(view.mangaCover)

            view.containerMangaName.isClickable = false

            setContainerClickListener(view, title, description, chapters, imageUrl)
  }

        private fun setContainerClickListener(view: SeriesSearchSelectorBinding,
            title: String?,
            description: String?,
            chapters: Int?,
            imageUrl: String?)
        {
          view.container.setOnClickListener
          {
            model.title.value = title model.description.value
                = description model.chapters.value
                = chapters model.imageUri.value
                = Uri.parse (imageUrl) resultAction ()
          }
        }
}
```

## Uso di Safe Args

Nel progetto abbiamo dovuto trasferire alcuni dati tra due fragment,
come indicato nella documentazione Android abbiamo deciso di usare il
*navigation graph*, quindi vincolando i dati ad avere un determinato
tipo.\
Questo vincolo è stato possibile grazie all'utilizzo del plug in [Safe
Args](https://developer.android.com/guide/navigation/use-graph/pass-data#Safe-args)
che ci ha permesso di specificare delle *action* con un paylod di dati
tipizzati.\

::: center
![image](action_navgraph.png)
:::

## Backup Del DB

Utilizzando le [funzionalità
native](https://developer.android.com/guide/topics/data/autobackup) di
Android abbiamo implementato un sistema di Backup che permette
all'utente di spostare la reading list senza bisogno di esportare alcun
file.\
Abbiamo usato le proprietà del manifest per indicare ad Android di
effettuare il back-up dei file dell'applicazione e di permettere il
trasferimento dei file quando si avvicina un nuovo dispositivo da
inizializzare.

## Reader {#reader-1}

I *cbz* vengono prima decompressi in cache, cosi da non occupare troppa
RAM, una volta fatto ciò, i file vengono converti durante l'esecuzione
in Bitmap ridimensionate per coprire più superficie possibile.\
Successivamente le Bitmap vengono gestite sempre da *Glide*, questo ci
assicura un'esecuzione asincrona e minimizza il codice da sceivere.\
Abbiamo anche implementato delle variabili per tenere conto della pagina
alla quale è arrivato l'utente, queste variabili sono servite anche per
la produzione della barra di progresso nella selezione dei capitoli.

# Valutazione

Abbiamo intervistato un tester della nostra applicazione e gli abbiamo
chiesto di redigere una breve recensione della nostra applicazione.\
Il tester non è stato scelto a caso, infatti è un appassionato di
lettura di fumetti, anche in formato digitale.

## Review

Nel complesso ho trovato l'applicazione molto fluida da usare, non ho
avuto troppi problemi a capire dove premere per aggiungere dei capitoli
o delle serie.\
Considero non troppo azzeccato il fitting in larghezza delle
splash-page, larghe il doppio rispetto le singole tavole. Per questo
motivo, risulta spesso difficile leggere i balloon più piccoli, avrei
preferito l'implementazione di una funzione pintch to zoom.\
In alcuni casi ho trovato che gli elementi a schermo fossero troppo
grandi e che in caso di rotazione dello schermo andassero a ingrandire
ancora di più. In un futuro aggiornamento, vorrei fosse introdotta la
modalità di lettura "endless", i.e. la possibilità di leggere i fumetti
spostandosi con "swipe" verso l'alto (in maniera non dissimile alle chat
di un'app di messaggistica).\

# Analisi critica dei limiti dell'applicazione

Per quanto strutturata e testata l'applicazione presenta margini di
miglioramento.\
Primo tra tutti lo stile grafico, che non fornisce un'identità propria e
purtroppo non è ben integrabile con il nuovo paradigma di
colorazione [@matDesColor] basato sul wallpaper, introdotto in Material
Design 3.\
Su suggerimento dei docenti non abbiamo implementato la creazione e la
gestione di un account.\
Il salvare solo l'URI dei file *.cbz* potrebbe portare alla perdita di
dati, ma abbiamo preferito questo rispetto all'"esplosione" dello spazio
richiesto dall'app.\
Per quanto riguarda il reader e la disponibiltà di titoli:

-   L'implementazione del pintch to zoom nella sezione di reading del
    manga.

-   L'attivazione di un opt in per visualizzare anche fumetti per
    adulti.

-   L'ampliamento del database per il supporto anche ai comic
    occidentali.

Purtroppo abbiamo notato anche problemi che non dipendono da noi, quando
avviamo l'intent di selezione di un file cbz la selezione è resa
possibile dai MIME type, quindi se un file explorer non li implementa
non è possibile selezionare alcun file.
