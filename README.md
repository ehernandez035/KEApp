# KEApp

KEApp Android sistemarako aplikazioa garatzeko honako egitura jarraitu da:

- `SplashActivity`: aplikazioa irekitzerakoan kargatzen ari den animazioa erakusteko.
- `RegisterActivity`: aplikazioan kontua sortzeko.
- `LoginActivity`: saioa hasteko.
- `MainActivity`: orri nagusia erakusteko.    
- `SettingsActivity`: aplikazioaren konfigurazio orokorra aldatzeko.
- `GalderaActivity`: galdera bakoitza erakusteko. Galdetegia bukatzerakoan, erantzun-lista bidaliko dio zerbitzariari eta honek zenbat erantzun zuzen dauden erantzungo du.
- `GaldetegiZerrendaActivity`: galdetegi guztiak erakusteko. Galdetegi bat klikatzerakoan, honen galderak kargatuko ditu.
- `KodDekodActivity`: Bihurgailua erakusteko. Bertan hiru \textit{fragment} daude:
   - `CalculatorFragment`: datu-mota batetik beste batera bihurtzeko leihoa.
   - `DeskodFragment`: zenbakizko deskodeketa gauzatzeko leioa.
   - `KodFragment`: zenbakizko kodeketa gauzatzeko leihoa.

- `KonfigInfoActivity`: profileko datuak erakusteko.
- `ProgActivity`: programak idazteko.
   - `ParamFragment`: programak sortu aurretik, honetan erabiliko den alfabetoa eta parametroen hasieraketa gauzatzeko leihoa.
   - `ProgFragment`: programak idazteko leihoa.

- `RankingActivity`: puntuazio altuena duten 10 erabiltzaileak eta erabiltzailearen posizioa erakusteko.

Proiektuan hainbat Adapter erabili dira:

- **Kodeta eta deskodeketan**: kodeketan, sarrerako zenbakizko hitzak eskatzeko eta deskodeketan, irteerako hitzak erakusteko. 
- **Errore lista**: programa bat exekutatzerakoan, honetan erroreak egon badira, hauen deskribapena eta posizioa erakusteko. 
- **Galdetegietan**: galdetegi zerrenda erakusterakoan, galdetegi bakoitzaren informazioa erakusteko.
- **Parametroetan**: programak idazterako orduan, parametroen balioak hasieratzeko.
- **Programak gordetzeko eta kargatzeko**: erabiltzaileak gordetako programak zerrendaturik agertuko dira programak kargatzeko botoia sakatuz gero.

Kodearen antolakuntza errazteko asmoz, ondorengo klaseak sortu dira:

- `Galdera`: galdera bakoitzak dituen elementuen atzipena errazteko. Galdera bakoitza identifikadoreaz, 4 erantzun posiblez eta enuntziatuaz osatuta dago.
- `Quizz`: galdetegi bakoitzaren identifikadorea, erabiltzaileak egindako galdetegietan lortutako erantzun zuzenen kopurua, deskripzioa eta galderen kopuru totala gordetzen dira klasean.
- `RunPrograma`: while- ala makro-programak exekutatzeko. Programa mota bakoitzaren araberako analizatzaile sintaktikoa sortuko du.
- `ServerRequest`: zerbitzariari deia egiterako orduan, kodea laburtzeko asmoz sortutako klasea.
- `SharedPrefManager`: aplikazioaren saio guztian zehar mantenduko diren datuak gordetzeko. Erabiltzailearen datuak behin eta berriz zerbitzariari eskatu beharrean, hauek saioa hastean gordeko dira eta saioa mantendu bitartean gordeko dira.
