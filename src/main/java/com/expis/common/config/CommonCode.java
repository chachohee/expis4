package com.expis.common.config;

import org.springframework.beans.factory.annotation.Value;

public class CommonCode {
	
	/* 파일 포멧 형식 */
	@Value("${}")
	private static String projectName;
	
	public static String[][] arrToList = {
		{"BC146A69F3B24905B90C58866E9C79AA","교범 목록","SubSystem"},
		{"2612524630BB413d85A7F9B4D0A3E631","비행기술서","TO"},
		{"1B2FD1EA97C441f998B49F42CD170FC8","작업단위부호교범","SubSystem"},
		{"E5728E42EC964235830CE05103E1880D","1T-50A-06","TO"},
		{"E30D89A2D89341b2BB2FFCFE15540F01","비행 교범","System"},
		{"648D744AD756465289F5F5C81C063616","비행 교범","System"},
		{"DBA52D624400499196826A03AB562D6E","1T-50A-1","TO"},
		{"DBA52D624400499196826A03AB562D6F","1T-50A-1_E","TO"},
		{"DBA52D624400499196826A03AB562D6F22","1T-50A-1SS-1_E","TO"},
		{"DBB89A6CAA904105A7EA12C6AF7A6F27","1T-50A-1CL-1","TO"},
		{"DBB89A6CAA904105A7EA12C6AF7A6F28","1T-50A-1CL-1_E","TO"},
		{"4EAFCB9D677E4878B342553A321B5A0D","중량교범","System"},
		{"ccd66417238a4056a424c568c48ef771","1T-50A-5-1","TO"},
		{"ccd66417238a4056a424c568c48ef772","1T-50A-5-1_E","TO"},
		{"F3BFC3B73281491cB46F225CABB0CED9","1T-50A-5-2","TO"},
		{"F3BFC3B73281491cB46F225CABB0CED0","1T-50A-5-2_E","TO"},
		{"66D543C5E0CE4d7c922E4E696F0EEE09","수락 및 기능 점검","System"},
		{"0C4C0C704A564cf8B439F9181C92FC3B","1T-50A-6CF-1","TO"},
		{"0C4C0C704A564cf8B439F9181C92FC3C","1T-50A-6CF-1_E","TO"},
		{"B56D42F62AA948cfBFA2A9732773AFF0","1T-50A-6CL-1","TO"},
		{"B56D42F62AA948cfBFA2A9732773AFF1","1T-50A-6CL-1_E","TO"},
		{"2b4784b5be534a1e8f777584412ad089","무장발사","System"},
		{"51364644099e467da705bd37b29b4a6b","1T-50C-34-1-1","TO"},
		{"51364644099e467da705bd37b29b4a6c","1T-50C-34-1-1_E","TO"},
		{"a87d1d51bb8f41cc9e42afb4b51731eb","1T-50C-34-1-2","TO"},
		{"a87d1d51bb8f41cc9e42afb4b51731ec","1T-50C-34-1-2_E","TO"},
		{"bcd5bc0f1f6643f39a24738991c15181","1T-50C-34-1-1CL-1","TO"},
		{"bcd5bc0f1f6643f39a24738991c15182","1T-50C-34-1-1CL-1_E","TO"},
		{"8629F128CD6D4b2796C2BFCF202A25FB","부대급 정비교범","System"},
		{"0647A96729534dbfBF9167CF898DBAF3","결함보고","SubSystem"},
		{"6B407E5627914f92AAE377DD6AAFC667","1T-50A-2-00FR-00-1","TO"},
		{"D4046F33A5CD45b19BE4E2D46B1DE6FB","항공기 일반","SubSystem"},
		{"D5E57B65BBC24e04963F1D7FA8973794","1T-50A-2-00GV-00-1","TO"},
		{"8BDD978F981445778877B609528BFC53","작업지침서_색인","SubSystem"},
		{"4AAB649C617A472e966D880104DCB7B8","1T-50A-2-00JG-00-1","TO"},
		{"3B88331DC9B841bc93D5F5212F3F81DA","작업지침서_일반","SubSystem"},
		{"3F438C17AABE4815A304F960B4CB8DC1","1T-50A-2-07JG-00-1","TO"},
		{"FD7A965897C343349A02A4B4B3C934BA","1T-50A-2-09JG-00-1","TO"},
		{"694D89FFB9B444e083557A96D47FDE0A","1T-50A-2-10JG-00-1","TO"},
		{"B62BCDCD3A60474aBA11C3CCFA2A046B","1T-50A-2-12JG-00-1","TO"},
		{"2B639193502E4acbBB27FFCE524038DC","배선자료_WD","Component"},
		{"88A5AB456B004e298BC409328D1B048C","1T-50A-2-00WD-00-1","TO"},
		{"8F75AB88D49245acA188760D7CEBF5D5","1T-50A-2-00WD-00-2","TO"},
		{"6569221BBED642e28231B21BA514D8CA","1T-50A-2-00WD-00-3","TO"},
		{"7CBD71554D69414a971EC2D8E4EF1639","1T-50A-2-00WD-00-4","TO"},
		{"3E5DC8FFEB2943b6BFED281E7B5FF621","계통정비_GS/FI/JG","SubAssembly"},
		{"670403dd3bd04e29bd80f7cfdc94c6fa","20_시범 기동","Component"},
		{"9c6bf6990c454a5583db70783304d5a9","1T-50B-2-20GS-00-1","TO"},
		{"9e70eceb099249038f3ec13815e20415","1T-50B-2-20JG-10-1","TO"},
		{"02478d4a4cf64ecf9dec9b5cbbe331dc","1T-50B-2-20JG-20-1","TO"},
		{"a3369dbf10a54f3ca263235ea71f1ba2","1T-50B-2-20JG-30-1","TO"},
		{"5908b36289d242618643a297b40a746a","1T-50B-2-20JG-40-1","TO"},
		{"57884c0e7b764643a72f4f8b201f2d3f","1T-50B-2-20FI-00-1","TO"},
		{"74D0105CA4CC42de8920FF42E3F96AA1","21_공기조화","Component"},
		{"5A890915D3C54f59A56B6E183B9F9DD2","1T-50A-2-21GS-00-1","TO"},
		{"11BC091C6F424c9196864AF5F1990316","1T-50A-2-21JG-00-1","TO"},
		{"D1DF979C9B544acf94963DE698E0CB19","1T-50A-2-21JG-10-1","TO"},
		{"11C695A6B8BA42eaB26354F7857CE1DF","1T-50A-2-21JG-20-1","TO"},
		{"10B17BB3341F4d74944F9A8BF0730861","1T-50A-2-21JG-30-1","TO"},
		{"CEE17A701F4348d494B553E41E3483BB","1T-50A-2-21JG-50-1","TO"},
		{"ED1CBE67842B43eaA1B3A77FA589CFBA","1T-50A-2-21JG-60-1","TO"},
		{"F44C81CEB73C4BF7A5EB3EE2F65ABC59","1T-50A-2-21FI-00-1","TO"},
		{"F39599A08B0948f3BE215FD460A87C47","23_통신","Component"},
		{"2274A12FE01340ff8E4989F08B3C3207","1T-50A-2-23GS-00-1","TO"},
		{"B44216324BE944d0853552B781B76E53","1T-50A-2-23JG-20-1","TO"},
		{"499DA5B5BE074855BEEC6E6403C53216","1T-50A-2-23JG-40-1","TO"},
		{"B9F150AF42844CACA738C1773F875937","1T-50A-2-23FI-00-1","TO"},
		{"3C2C93853AA843dd8266E60BE9C2BCD5","24_전기동력","Component"},
		{"675DA0137D0D44e4B448EC3282F76590","1T-50A-2-24GS-00-1","TO"},
		{"BCE23E2FC43C476195C67D8790292BE0","1T-50A-2-24JG-00-1","TO"},
		{"8DB81A40E79046ffA978C34EAE1FAD25","1T-50A-2-24JG-20-1","TO"},
		{"4095A28C4F564580BDDCD9EA82AEB707","1T-50A-2-24JG-30-1","TO"},
		{"0993345204A74a7e9CA6AF6E51A6985B","1T-50A-2-24JG-40-1","TO"},
		{"CA589449E258424bB54A317A1401E1B3","1T-50A-2-24JG-50-1","TO"},
		{"442139B46AFA4AEAB9ABC8A65C9180CF","1T-50A-2-24FI-00-1","TO"},
		{"6E2E82FA58CD4c1cA2D0163D876D183D","26_화재방지","Component"},
		{"B691890963E74e44A4186D87CA21F47F","1T-50A-2-26GS-00-1","TO"},
		{"6BD5061F0E2648db9A5AB942387BA2D2","1T-50A-2-26JG-10-1","TO"},
		{"D503DE053C1D4ECC90706064460A7509","1T-50A-2-26FI-00-1","TO"},
		{"1E2542AD52DC49a0B6AF62284B93F3E2","27_비행제어","Component"},
		{"E7E22DDE534B4952BF6F732024C71A29","1T-50A-2-27GS-00-1","TO"},
		{"86C6726D87A54b9cA25ACB4F574F8C4F","1T-50A-2-27JG-00-1","TO"},
		{"DCBD289D77F04d38B29C1CDB3D2AE1A8","1T-50A-2-27JG-10-1","TO"},
		{"E4455BAAEAD3436aB38CDFDCB073621E","1T-50A-2-27JG-20-1","TO"},
		{"1ADD24E7463146f38E50C19A5E7A1C29","1T-50A-2-27JG-40-1","TO"},
		{"6CAB5DB720984ab5A2B9FC0DD76EA7F6","1T-50A-2-27JG-60-1","TO"},
		{"FA95CDEDC64448efAA264826BD7DDABF","1T-50A-2-27JG-80-1","TO"},
		{"3A4ED93F9A464A0293351E5B15909D66","1T-50A-2-27FI-00-1","TO"},
		{"7622C5EF3D27460b964C894DB381159B","28_연료","Component"},
		{"5F751C9AF86D4094B7BB30BAE6798923","1T-50A-2-28GS-00-1","TO"},
		{"753BEEAD39064cf1BF86A7EE815A6966","1T-50A-2-28JG-10-1","TO"},
		{"F65F12E5D0BF4357850F8903AF8DAB6F","1T-50A-2-28JG-20-1","TO"},
		{"62520DFE74E3473f9619F2E920C3B387","1T-50A-2-28JG-40-1","TO"},
		{"E8AA176982AF4b96963CF55832966C4A","1T-50A-2-28FI-00-1","TO"},
		{"B16943F0A0D840e2BEDDDB09D9B1587B","29_유압","Component"},
		{"7E5ED9511D95480f901C3BD3362F9C36","1T-50A-2-29GS-00-1","TO"},
		{"80DBED56E4A4471eB2D0FD39498A1784","1T-50A-2-29JG-00-1","TO"},
		{"2EF759AB52344a5a91888E4405508A55","1T-50A-2-29JG-10-1","TO"},
		{"9539D000052A4b90BFC8F052B646C23A","1T-50A-2-29JG-10-2","TO"},
		{"fffa18f69a214d189c8d0c0ea925f0f3","1T-50A-2-29JG-30-1","TO"},
		{"5B66417B45534831B631D87A6892877E","1T-50A-2-29FI-00-1","TO"},
		{"3D650F0140C74df0A3D6264CB742BBDA","30_방빙","Component"},
		{"46F8B17CFE5E4db49E07F5A8947160DB","1T-50A-2-30GS-00-1","TO"},
		{"5279F0C8C3D44b3b892ADCD1BE158B2E","1T-50A-2-30JG-00-1","TO"},
		{"4A5E21260C2B44C1945478DDA2992341","1T-50A-2-30FI-00-1","TO"},
		{"FAF207312A1B4170AE8845D174794DD5","31_비행자료기록","Component"},
		{"158C055E0CD8412a9CBECB9C6BCAD403","1T-50A-2-31GS-00-1","TO"},
		{"9449FBBCDA054d2bBC34BFCBB31BA02C","1T-50A-2-31JG-00-1","TO"},
		{"8FDF6499DEB84F709EECF9F2A19802C3","1T-50A-2-31FI-00-1","TO"},
		{"BD85B3B1A4E64132A84E8F38B39BAD97","32_착륙기어","Component"},
		{"B0400509B8044c448A3FD87744EAEF31","1T-50A-2-32GS-00-1","TO"},
		{"5D801F50C8E14a8bBF0C0391AF33A288","1T-50A-2-32JG-00-1","TO"},
		{"16F551D7265F4bcdBFB02DD59D7AAE70","1T-50A-2-32JG-10-1","TO"},
		{"3E1B3411C0344e4b83BFFD0C87D2EABA","1T-50A-2-32JG-20-1","TO"},
		{"4AD8098A1BE742278610018C8C5F9092","1T-50A-2-32JG-30-1","TO"},
		{"B7045ABEA7594af2972687C6BFBE5AB6","1T-50A-2-32JG-40-1","TO"},
		{"75BD4909BC0B46c3BE8036FFF9E2508E","1T-50A-2-32JG-50-1","TO"},
		{"B5E4D23E1F524a3b9A741A6BE7F9CB61","1T-50A-2-32JG-60-1","TO"},
		{"FDEB91EBB4A747cc85663B66491A6910","1T-50A-2-32JG-90-1","TO"},
		{"EEEBA812BD794A168CFD0DD81F09D71F","1T-50A-2-32FI-00-1","TO"},
		{"04B04DF74CE44f4b9C54BE1910A33B08","33_조명","Component"},
		{"1DEE188F2CC84dbcAD0A0F902116171E","1T-50A-2-33GS-00-1","TO"},
		{"3399B18339F14d2aA7DA479773D7F265","1T-50A-2-33JG-00-1","TO"},
		{"B57646B63A0543648923BE9DD7341112","1T-50A-2-33JG-10-1","TO"},
		{"A784648FFC004fe09BE35132C64B52FC","1T-50A-2-33JG-40-1","TO"},
		{"225B2E227C7F455FB7DDB376EBD00739","1T-50A-2-33FI-00-1","TO"},
		{"CF6A7618EC6B4431AD12CCD4F8CF263A","34_항법","Component"},
		{"D93AF1234EA44e7eA1746EF581EF476B","1T-50A-2-34GS-00-1","TO"},
		{"1a8944a2e82e47cd9162ca9830b64d2b","1T-50A-2-34JG-10-1","TO"},
		{"3024508628484844B3C2649ABEAD1E94","1T-50A-2-34JG-20-1","TO"},
		{"CE483B9708C44c5295245B2B4F99FFC1","1T-50A-2-34JG-30-1","TO"},
		{"4933D1A6C98E417b976586CE9B148EC3","1T-50A-2-34JG-50-1","TO"},
		{"4E49ACF8904F4AFAB63DA2ABC870454E","1T-50A-2-34FI-00-1","TO"},
		{"6D48D44F6BEE4c78ACA2EFB3BBF836B9","35_산소","Component"},
		{"4875DFDC0DFC4dec8B1C69390E6ED60F","1T-50A-2-35GS-00-1","TO"},
		{"1D3EB60C4B734962B2B9DE43581D7FE9","1T-50A-2-35JG-00-1","TO"},
		{"2FBAC9F2AAA64C9F8BDABAA1EBBC7258","1T-50A-2-35FI-00-1","TO"},
		{"D591C95D033645a5B5D99918E84C94DE","36_공압","Component"},
		{"99DEBC2F320B48be97CEE7C9CD0CED21","1T-50A-2-36GS-00-1","TO"},
		{"0497617BE9374447B053CD5581B929E3","1T-50A-2-36JG-10-1","TO"},
		{"FEF401B48C4B438582ABFA38462A8468","1T-50A-2-36FI-00-1","TO"},
		{"5A3239AC5FC549bdAE420DA774924368","39_전원분배","Component"},
		{"0C89C23EF87C4956BE8531EED7E61E22","1T-50A-2-39JG-00-1","TO"},
		{"A1E334CC8A084395B7820230095ED63A","49_비상동력","Component"},
		{"A3345A01A4A34756BEB9E89F2494A48A","1T-50A-2-49GS-00-1","TO"},
		{"BAD97DD56EDA4c43B0DCBFBCA5127106","1T-50A-2-49JG-00-1","TO"},
		{"35198D2877F3498CA3FBBB8A10B81A78","1T-50A-2-49FI-00-1","TO"},
		{"ECCF31F9C5B443b3AE801F915270C450","52_점검창","Component"},
		{"58FC37D6F18F434482FA26FC7334E2FC","1T-50A-2-52JG-00-1","TO"},
		{"A4229BD1FF8F4f71B2DF2BE915F5F6EB","53_점검판","Component"},
		{"D25AA5BDC5F14795A268B8371A945EB7","1T-50A-2-53JG-00-1","TO"},
		{"20FF860A44714953B788B83464167A3D","70~79_동력장치 - 엔진","Component"},
		{"1631DDCD043A4c2eA5E1E995B5F96D53","1T-50A-2-70GS-00-1","TO"},
		{"B61601099BA140079ED341934F644EC7","1T-50A-2-70JG-00-1","TO"},
		{"E70E8F55E7D14f49A06253A535BA49AA","1T-50A-2-70JG-10-1","TO"},
		{"CF874B46540B4ba7B69E641E5284AB42","1T-50A-2-71JG-00-1","TO"},
		{"B5536CB607C1458fADCD7099B233A6E4","1T-50A-2-73JG-00-1","TO"},
		{"E86B0EFD39F74b0dBE147A177BD5C636","1T-50A-2-74JG-00-1","TO"},
		{"16041FDCBADF49c789D4B129993E501E","1T-50A-2-75JG-00-1","TO"},
		{"7AD4BB81EF5C4224951B008CF394C060","1T-50A-2-76JG-00-1","TO"},
		{"AE0D809D07F1423cB1AC5E94C40E86AC","1T-50A-2-77JG-00-1","TO"},
		{"33E39D078F02401781FC378641CC28F1","1T-50A-2-78JG-00-1","TO"},
		{"1DD1870289054a6fB3A65154267E20D9","1T-50A-2-79JG-00-1","TO"},
		{"81A4CA60F4E04C82807DD0867A5FD574","1T-50A-2-70FI-00-1","TO"},
		{"7BAB53ADE08F4bb8B64BC0FC22CE3490","80_보조동력","Component"},
		{"2A8EEC582D634c3e92F309EAACB00476","1T-50A-2-80GS-00-1","TO"},
		{"7FF4C67DD7014304A1B30F5DCED6E879","1T-50A-2-80JG-00-1","TO"},
		{"9D30D63CCB5B453cA52032F58FA9366E","1T-50A-2-80JG-10-1","TO"},
		{"7BC6C28F13A140ACA9CB57F51B601EBF","1T-50A-2-80FI-00-1","TO"},
		{"8A3CC740C38148fe8F847E02F9268C5B","94_무장","Component"},
		{"B9F8907B54524425A895341B6A0CF29A","1T-50A-2-94GS-00-1","TO"},
		{"EE5352FEDE6740c6957086E9C636DCDA","1T-50A-2-94JG-00-1","TO"},
		{"BCDD849FD2F0424b8F4D3A624BAA7476","1T-50A-2-94JG-10-1","TO"},
		{"6DC6EB53B2804d358B0AF565F43D9ED3","1T-50A-2-94JG-30-1","TO"},
		{"b4a2ab7309314fcb9135d5434a45ea84","1T-50C-2-94JG-50-1","TO"},
		{"9D87876E69C54d7eAD860C6424673513","1T-50A-2-94JG-60-1","TO"},
		{"A3EBF1510DCB44a7A7FF4236B97B8BED","1T-50A-2-94JG-70-1","TO"},
		{"656B4D7614AB4284B0FD71AD33429F4A","1T-50A-2-94FI-00-1","TO"},
		{"1B1DEDB910124f22B05B872494233B08","95_조종사 탈출 및 안전","Component"},
		{"CB970619CFA3440aA615060881EB0421","1T-50A-2-95GS-00-1","TO"},
		{"22EE0A6698F84961AEE1157359D652F6","1T-50A-2-95JG-00-1","TO"},
		{"A89F974557B34e8cBC00CD85B36D2F21","1T-50A-2-95JG-10-1","TO"},
		{"DC842F19F6D84d469AD850720168E7CC","1T-50A-2-95JG-20-1","TO"},
		{"71A02875F05848beA742EFFF406996C5","1T-50A-2-95JG-50-1","TO"},
		{"57FA657A3DC54B109AFF6F8FFCAAB8C6","1T-50A-2-95FI-00-1","TO"},
		{"289283D70A8F4d7cB3A814940995ECC0","97_영상기록","Component"},
		{"DD201AD7D90646ea94D20E36532DCA29","1T-50A-2-97GS-00-1","TO"},
		{"52533B4C65AE41af9A6959C4BE4866B6","1T-50A-2-97JG-00-1","TO"},
		{"0E401570F67A4183A076C91B4696F9F3","1T-50A-2-97FI-00-1","TO"},
		{"B03717479C99467dAF5BFBD8545607A4","기골수리","SubSystem"},
		{"6E72E0915B2549cd8F5E827F6F89CDA9","1T-50A-3-1","TO"},
		{"770A257483184989ADB1F346FFF38128","1T-50A-3-2","TO"},
		{"EBEF34A9B0834657AA5DB683B52AD4EC","1T-50A-3-3","TO"},
		{"ED7C247A80314b44841CE39A525C28E0","1T-50A-3-4","TO"},
		{"7D7B14FDB565408bBE8C263503F1554A","1T-50A-3-5","TO"},
		{"00C85D84BE8B4e37A87C95D4EC165127","부분품도해명세서_IPB","SubSystem"},
		{"060BDC23C81B427eB1829FF08C2E8FD5","색인","SubSystem"},
		{"B864F2F9E0CB482bB12F0D5CA515F14E","1T-50A-4-1","TO"},
		{"A7028C61A15C4c3f859C2AEBB427FD3E","1T-50A-4-2","TO"},
		{"ED4CE831D5DD4318B09E3F2637597452","1T-50A-4-3","TO"},
		{"CE693AE6497B4014876EB340E3DA4F80","1T-50A-4-4","TO"},
		{"839AB06CB5BD45148D6C7D7A1F45659D","계통","SubSystem"},
		{"49cba03e4d8b43a0bac6e316cf2d7664","1T-50B-4-20","TO"},
		{"72450C664DEB49acB73B3BD5264C36CE","1T-50A-4-21","TO"},
		{"02D5C841698E4760822278176B8B408E","1T-50A-4-23","TO"},
		{"C30BEB01B9834b13BE70512A8A265ACE","1T-50A-4-24","TO"},
		{"209DA4988264426786CFD64B1E3E5C42","1T-50A-4-26","TO"},
		{"08803CEBF58941828BA1BAFAE28480E8","1T-50A-4-27","TO"},
		{"C89856FCF7E849de8BFDEE211C9FDD97","1T-50A-4-28","TO"},
		{"CCFA83DA0BBB4e389FA609BA6692ABDC","1T-50A-4-29","TO"},
		{"09629D5A1D4D44deACD2CBB3080B840A","1T-50A-4-30","TO"},
		{"F4B35635277146d19DD94EF93DB3B83A","1T-50A-4-31","TO"},
		{"61F5F2F6901A417684E77A20E0557FB1","1T-50A-4-32","TO"},
		{"A43032276129465a869D34A70B0510D8","1T-50A-4-33","TO"},
		{"343E0FA6D8344d8392A77C67F98FFE73","1T-50A-4-34","TO"},
		{"DBF4310C5BFB4b1c95A7E65F8A8BDB03","1T-50A-4-35","TO"},
		{"7F01735CA3DC48939832C9AB08DDC877","1T-50A-4-36","TO"},
		{"9DBB23ED6CEC4e838FFDDF8E8F9092C9","1T-50A-4-39","TO"},
		{"122DA88F40E1487cA6AC31A77081ECDB","1T-50A-4-49","TO"},
		{"C3BA4823700847d58E3A8122458B4E3C","1T-50A-4-51","TO"},
		{"303EB37C279D4786BEE2E51314B8AC06","1T-50A-4-70","TO"},
		{"FE9DC4698CA0435a826D37A8040896E2","1T-50A-4-80","TO"},
		{"D06F28755E88435dBA8F468EF697C57A","1T-50A-4-94","TO"},
		{"013C9E16F09F43dcA723A5C9075E0631","1T-50A-4-95","TO"},
		{"7D320BDD61194f41BC7DD0384E975207","1T-50A-4-97","TO"},
		{"EBF56495871D4b10AD98E31A6EEBBDA0","계획검사","SubSystem"},
		{"1F5E9E624D3A45469262EFB30932E1EC","1T-50A-6","TO"},
		{"DC6F801DE9EF4322A56C2AE8F3063992","1T-50A-6WC-1","TO"},
		{"E1903054FFD94821ACCA648FC025F0CD","1T-50A-6WC-2","TO"},
		{"6A27F99FC1034f378DAA78923D310D57","1T-50A-6WC-4","TO"},
		{"934C5C1CC2694e2185FEE14A5F06FDCE","비행 운용 프로그램 유효성 및 로딩 지침서","SubSystem"},
		{"3DD19DE5FCB540ceAB965D47C3679192","1T-50A-8-1","TO"},
		{"E76818B0BEFF475a9D71866117DABDAB","현수조사","SubSystem"},
		{"0C4C60E6A1134d01984AE6F2E97E3061","1T-50A-21","TO"},
		{"14C151FAC1C04e0e8B8A145AEE7724BA","방부처리","SubSystem"},
		{"38FF6D5B925E4b1fBFD6985A72718EB8","1T-50A-23","TO"},
		{"ae40dbddd4b143f68d34ad407c6287a6","비핵탄약","SubSystem"},
		{"b93a65d7ce9e4563a28036ad1c319eed","1T-50C-33-1-2","TO"},
		{"318b4aa87d9a461dbf9a3dd0b340b4cb","1T-50C-33-1-2CL-1","TO"},
		{"326b8bc44dff4ddab381c2acfbdff6f8","1T-50C-33-1-2CL-2","TO"},
		{"322b40f295154a14894f2bec49d56189","1T-50C-33-1-2CL-3","TO"},
		{"ace915e25e5446708d00bc3944e26dab","1T-50C-33-1-2CL-4","TO"},
		{"efdb20c267274ee0843a90bf3a348b47","1T-50C-33-1-2CL-10","TO"},
		{"3599117a67694f599c907618143d72cc","1T-50C-33-1-2CL-12","TO"},
		{"aa960769619a4fd59ad29c74b13cdf38","1T-50C-33-1-2CL-14","TO"},
		{"3b21e82dc5c54ed481c9bf036fc2d004","1T-50C-33-1-2CL-17","TO"},
		{"6219eb10790449d7bb453b047152b290","1T-50C-33-1-4","TO"},
		{"2e28d4a993684115a819266b8c375785","1T-50C-33-1-4CL-1","TO"},
		{"90552224E78B4ee192B1198F9B894E34","비파괴검사","SubSystem"},
		{"D76FCE81DFEA40a9BE96680023A3181B","1T-50A-36","TO"},
		{"d6223ba51d6249f0947c3ff949c3b2aa","ASIP","SubSystem"},
		{"4a71ff23916f4552b2f0d0ea92efe730","1T-50A-38","TO"},
		{"d6234ba51d6350f0947c3ff949c3b2aa","항공기 전시 파손 수리","SubSystem"},
		{"5b71ff23016f4552b2f0d1fa92efe730","1T-50C-39","TO"},
		{"7F81AFC1B88C4e19B175D8A48C7942DC","야전급 정비교범","System"},
		{"7BB847937FDB420c9CA0BF57B26B1EE8","2계열_항공 엔진 및 관련 장비","SubSystem"},
		{"b677bac6322848898172c1fe62a3f6aa","2JA9-1-2","TO"},
		{"bc710f8ab2ca444d901cbd6ae82b7e6d","2JA9-1-4","TO"},
		{"f2efa02a64e64f9aa620e1bd2126bba8","2J-F404-2-1","TO"},
		{"84d9e0a9d8bd4dcd9f3793960e474706","터보팬 엔진","TO"},
		{"e56b7fe03bee4666a62e387d4ddaa673","2J-F404-2-3","TO"},
		{"58f7bfaa58ed48e88fe5c8cee157c45a","2J-F404-2-4","TO"},
		{"7cfd670a8ad94ffcafc684685a09e41d","2J-F404-2-5","TO"},
		{"d230435d94004fd2827962100b9629f8","2J-F404-4-1","TO"},
		{"37d93e41668a4faa8adee910932bdb79","2J-F404-4-2","TO"},
		{"1b93caa849b94c99a0c51529039a77a2","2J-F404-6WC-1","TO"},
		{"2F7F3A45579C4961A845A00A2FDFE4D1","4계열_항공기 착륙 기어","SubSystem"},
		{"8B725499CEE34612A2F18843E4400792","전방 착륙기어 언락 작동기","TO"},
		{"003cb095eaf94f3fb61d9a5c151d9458","4AA1-1-4","TO"},
		{"4dd8dfb0dabc4c7a82184c9c54f38c64","4B1-2-2","TO"},
		{"dcc26be7dfd94ccbb9fbf8c9d60f5786","4B1-2-4","TO"},
		{"cf11949427b9496cb771a44dac42fab0","4BA4-117-2","TO"},
		{"de691f07986c4a289125827008a9caba","4BA4-117-4","TO"},
		{"d464d72c77c4459d87bfe169cd881393","4S1-2-2","TO"},
		{"dadcd2f7f4244a78920431242a5a2a2d","4S1-2-4","TO"},
		{"a56391b1fb6a404a9ac8efe88ee19868","4S1-3-2","TO"},
		{"7aa28ee7f41d42c8bb7c6ed07301742b","4S1-3-4","TO"},
		{"3bf883e7ee4141de9468f45bfb3fdf8b","4S1-4-2","TO"},
		{"3efc2edd42764fff850c5341d716c376","4S1-4-4","TO"},
		{"496bdf54aedc46b69e74d78deee259d8","4S2-2-2","TO"},
		{"f385832b4d344dcf9322ff44a40ad813","4S2-2-4","TO"},
		{"0a5cb87dc2754096a425d39ae57e5de8","4S2-3-2","TO"},
		{"97b2a920e4cf4682be97dd7bcd7a9e1f","4S2-3-4","TO"},
		{"63242c6ad0ec47319391bcfd9cff10d8","4S2-4-2","TO"},
		{"1013fd446c4e477e9285ed34da32bc5d","4S2-4-4","TO"},
		{"f5892a0b9f6945fc85c243038ce9763f","4SA2-3-2","TO"},
		{"4e228e6191fc402eb17ca809c69c4131","4SA2-3-4","TO"},
		{"c70309b4bb64424c897c252f69ee7318","4SA6-3-2","TO"},
		{"a7509e6e7b264fbe9de3fd2dba79074c","4SA6-3-4","TO"},
		{"7c4dbf3c8ba54ee38b9731b965e2d210","4W3-7-1143","TO"},
		{"414e303980f249ae8a6f5900bf41e999","4W3-7-2-2","TO"},
		{"F3CA078947744c579A9A414F7C7F6513","5계열_항공 계기","SubSystem"},
		{"E6D454105077492cADB035DA3897A906","5A13-5-1-2","TO"},
		{"1370c9652a3442e2bdc2d2fd1e77c15f","5A13-5-1-4","TO"},
		{"7734ce7862c640c98b671faa52add7dc","5N1-4-1-2","TO"},
		{"4b9793ad215d410cbf023926e85a218f","5N1-4-1-4","TO"},
		{"2271647bb16b4083963d94ea4318ab4e","5N1-4-1-8","TO"},
		{"b1eb719ab6c54c80b972acedf3780ce3","5A13-5-2-2","TO"},
		{"1e8575a7fa8344de909cbbac1652257a","5A13-5-2-4","TO"},
		{"b1eb719ab6c54c80b972acedf3780ce4","5A13-5-2-2KS","TO"},
		{"1e8575a7fa8344de909cbbac1652257b","5A13-5-2-4KS","TO"},
		{"88a52cdb61784122aac27df5cb5f9283","5A7-3-1-2","TO"},
		{"b470e2dc262148d9ab91de30ab2e4e47","5A7-3-1-4","TO"},
		{"7ee64aca41ec4a5d8e51e27f4a1d6a5e","5A7-3-1-8","TO"},
		{"7bb5b89f39c848e38ab95c64202b200a","5F8-3-1-2","TO"},
		{"8fc3c2d1e9c748d2a230a983577b3834","5F8-2-1-2","TO"},
		{"9f043cdd54464ffa9589740d797faec6","5F8-2-1-4","TO"},
		{"08525428ef0d44e3a012060693e72f66","5F8-2-1-8","TO"},
		{"46bdf02bb4e349cda0617f847cd1eb08","5L3-2-1-2","TO"},
		{"3f6701a00b7f48d6931dc7392a0af6ab","5L3-2-1-4","TO"},
		{"c1bc6c00ce1c4308932746bf680b4156","5N29-1-2","TO"},
		{"5ffa3f7a4f9d4ac28650d54a9f3daeb4","5N29-1-4","TO"},
		{"ee31a61a2ebe473dba7e5ed553306005","5N29-1-8","TO"},
		{"0b0a55c59e9a4799b9474c14421388b4","5N3-3-1-101","TO"},
		{"380127171e28447b919751b2df859cf4","6계열_항공기 및 미사일 연료 계통","SubSystem"},
		{"d411756cf6c04fc8bb5ba29a7f0d7926","6J14-2-1-2","TO"},
		{"7bc1a9c009e843efb28645c3d8280656","6J14-2-1-4","TO"},
		{"d422757cf6c04fc8bb5ba29b7f0d7027","6J14-2-1-2KS","TO"},
		{"7bc2a9c009e854efb28645c3d8280667","6J14-2-1-4KS","TO"},
		{"C8BEE52DF21D433fB5A6F2661A40D5C0","8계열_항공 전기 계통","SubSystem"},
		{"46FDE3E55E354c61BE39B38B1A648ED8","8A3-18-6-2","TO"},
		{"3FF66EBF87D64728A411BBD4B83CD805","8A3-18-6-4","TO"},
		{"46FDE3E55E354c61BE39B38B1A648ED9","8A3-18-6-2KS","TO"},
		{"3FF66EBF87D64728A411BBD4B83CD806","8A3-18-6-4KS","TO"},
		{"8b37218cb26d44baa6481504b59e4a9e","8A21-1-2","TO"},
		{"caf0ecae6ad9475e9f83ed34b883f888","8A21-1-4","TO"},
		{"8b37218cb26d44baa6481504b59e4a9f","8A21-1-2KS","TO"},
		{"caf0ecae6ad9475e9f83ed34b883f889","8A21-1-4KS","TO"},
		{"9a5b74a36e5d4d3c821a22a3fdaf5248","8A21-2-2","TO"},
		{"0bf2f54766e94a6c9b9561ed63474b08","8A21-2-4","TO"},
		{"9a5b74a36e5d4d3c821a22a3fdaf5249","8A21-2-2KS","TO"},
		{"0bf2f54766e94a6c9b9561ed63474b09","8A21-2-4KS","TO"},
		{"4e2bd663b2474b4bbb02d600bac9720d","8A21-3-2","TO"},
		{"911742deecc74e33a269c7ca3306d510","8A21-3-4","TO"},
		{"4e2bd663b2474b4bbb02d600bac9720e","8A21-3-2KS","TO"},
		{"911742deecc74e33a269c7ca3306d511","8A21-3-4KS","TO"},
		{"efb408a0f40d4cd589f7fdece9dd374a","8A3-18-1-2","TO"},
		{"65c09f4d085f4b699ea4d73d1b54683e","8A3-18-1-4","TO"},
		{"efb408a0f40d4cd589f7fdece9dd374b","8A3-18-1-2KS","TO"},
		{"65c09f4d085f4b699ea4d73d1b54683f","8A3-18-1-4KS","TO"},
		{"ed1f6b3b2c464c96999e0947edcd7085","8A3-18-2-2","TO"},
		{"f85198bc6ec34a6bb76ad21b98e95d1d","8A3-18-2-4","TO"},
		{"ed1f6b3b2c464c96999e0947edcd7086","8A3-18-2-2KS","TO"},
		{"f85198bc6ec34a6bb76ad21b98e95d1e","8A3-18-2-4KS","TO"},
		{"f3f33899be844d1ba10fb435a5bd7539","8A3-18-3-2","TO"},
		{"d5bae9047d1a49ed959cfed58cf33137","8A3-18-3-4","TO"},
		{"f3f33899be844d1ba10fb435a5bd7530","8A3-18-3-2KS","TO"},
		{"d5bae9047d1a49ed959cfed58cf33138","8A3-18-3-4KS","TO"},
		{"d883afe97e5b48f285f3dd9f5b5383bc","8A3-18-4-2","TO"},
		{"385fdd928f1e4ceba1c2dce08e18b253","8A3-18-4-4","TO"},
		{"d388afe97e5b48f528f3dd9f5b8533bd","8A3-18-4-2KS","TO"},
		{"538fdd892f1e4ceba1c2dce08e18b425","8A3-18-4-4KS","TO"},
		{"e31563352b584626b0ee863e71c1d4bd","8A3-18-5-2","TO"},
		{"df25d8b045374c24abf7ce373b689abd","8A3-18-5-4","TO"},
		{"e36153352b546826b0ee863e71c1d4be","8A3-18-5-2KS","TO"},
		{"df36d8b045375c24abf7ce473b789ace","8A3-18-5-4KS","TO"},
		{"0fcb1e964f46426a92276bc30c798c03","8C10-8-1-2","TO"},
		{"c4e26c2a8d9747f3b7809fc7566de935","8C10-8-1-4","TO"},
		{"0fcb1e964f46426a92276bc30c798c04","8C10-8-1-2KS","TO"},
		{"c4e26c2a8d9747f3b7809fc7566de936","8C10-8-1-4KS","TO"},
		{"21421aa3a9a94dde90087ae6610578d1","8C10-8-2-2","TO"},
		{"d034a8f8a8314d608b712aec95873813","8C10-8-2-4","TO"},
		{"21421aa3a9a94dde90087ae6610578d2","8C10-8-2-2KS","TO"},
		{"d034a8f8a8314d608b712aec95873814","8C10-8-2-4KS","TO"},
		{"03bb11545d9b4768b2a4abbe58ce31c3","8C11-6-1-2","TO"},
		{"97add7b9bfeb4595b4e9b800ee774f70","8C11-6-1-4","TO"},
		{"c61a8742ffdb4e6f810064dca84d922b","8C15-8-1-2","TO"},
		{"f055c9c4b5e74c74bd19964d22ac644c","8C15-8-1-4","TO"},
		{"78dd2d60bdc94eea90114a14d6aee64e","8C15-8-2-2","TO"},
		{"f35ae8503139450f97ce41ec870a0815","8C15-8-2-4","TO"},
		{"91fa48f475f24b3681fde75372c93a5d","8C15-8-3-2","TO"},
		{"65d3d9f5e9504061b217e23864396344","8C15-8-3-4","TO"},
		{"3a1e2fc6638e4c7b817523f56ade2b74","8C21-4-2","TO"},
		{"4b8f509776ac46879fdbf05ce0d9a969","8C21-4-4","TO"},
		{"3a1e2fc6638e4c7b817523f56ade2b75","8C21-4-2KS","TO"},
		{"4b8f509776ac46879fdbf05ce0d9a960","8C21-4-4KS","TO"},
		{"b7687fb5754a463196cf2a692f1f624d","8C21-5-2","TO"},
		{"e03b1daddb8f4a4198639164d1d96678","8C21-5-4","TO"},
		{"b7687fb5754a463196cf2a692f1f624e","8C21-5-2KS","TO"},
		{"e03b1daddb8f4a4198639164d1d96679","8C21-5-4KS","TO"},
		{"3b1a3366c4fa4045b41737dfa6c17c98","8C21-7-2","TO"},
		{"10084619b66b4334a6de725d012da1e3","8C21-7-4","TO"},
		{"3b1a3366c4fa4045b41737dfa6c17c99","8C21-11-2KS","TO"},
		{"10084619b66b4334a6de725d012da1e4","8C21-11-4KS","TO"},
		{"9306daf146d64e06bfb2dd39d1f6786e","8C21-8-2","TO"},
		{"4979b11f5973454c8b2630a70ae2dceb","8C21-8-4","TO"},
		{"94c546d3c44b4e27bce4b6197a697857","8C21-9-2","TO"},
		{"dc7cf47faab443d0b9e275fc14775ac3","8C21-9-4","TO"},
		{"be5d0505678e4af9b30b675ab00f5081","8C21-10-2","TO"},
		{"83f099d8e3084db2ba60a5949bd86601","8C21-10-4","TO"},
		{"99af7705d8864f1ca087beb0eada9bf6","8D1-61-1-2","TO"},
		{"29772fcd4f5543c69fac6fcd927b8581","8D1-61-1-4","TO"},
		{"99af7705d8864f1ca087beb0eada9bf7","8D1-61-1-2KS","TO"},
		{"29772fcd4f5543c69fac6fcd927b8582","8D1-61-1-4KS","TO"},
		{"7b0aed6eff2e4c7481b762482bd0a4af","8C21-6-2","TO"},
		{"cde24079fefe4a9997a52743a592c92a","8C21-6-4","TO"},
		{"7b0aed6eff2e4c7481b762482bd0a4ag","8C21-6-2KS","TO"},
		{"cde24079fefe4a9997a52743a592c92b","8C21-6-4KS","TO"},
		{"f9f8dc134c834d74866867fa257e7419","8D2-1-2","TO"},
		{"2f64a1f2766d4b2480eb05809de1bcca","8D2-1-4","TO"},
		{"EA4C596EA7B146f28382BF4010DBE793","9계열_항공기 및 미사일 유압, 공압 및 진공 계통","SubSystem"},
		{"f895615f9d22462fb7bddf431029c65b","9H1-2-1-2","TO"},
		{"aee912aa65644f4a860c2c25f60209e7","9H1-2-1-4","TO"},
		{"9C57485ADBB44cf38632334F5D3E518D","9H1-2-63-2","TO"},
		{"E9D81BA1461C463e9F2C6588B1F9A094","9H1-2-63-4","TO"},
		{"0A5D5D4D0DD6443196E25C4A06759432","9H1-2-5-143","TO"},
		{"D57920BADEAA4ab7BEA1F8F37CC5CFD6","9H2-3-2-2","TO"},
		{"A73B78B7576340b685AF1E91785F0F8F","9H2-3-2-4","TO"},
		{"34D9DAF8DDE043c29A3C8B47B18D7139","9H2-3-3-2","TO"},
		{"CB6A726B5C5C41c4ABB3998ECC338140","9H2-3-3-4","TO"},
		{"6f3c95ed69414bb69192832f36d78039","9H3-3-12-2","TO"},
		{"ebd3c1ca8a374da0acb88cac4fcfc9c4","9H3-3-22-2","TO"},
		{"a0deefa98fa94aacb26461ef95498d81","9H3-3-31-2","TO"},
		{"2306bb19ade24ce6b5194c5bbe4a5ceb","9H18-1-2","TO"},
		{"1068c94ec3b24c65bcc333986cc174d2","9H18-1-4","TO"},
		{"17b3fcaa24c7476db4142b8ac898aaa9","9H2-2-113-2","TO"},
		{"d520e0b67af2471ea1bc098149841660","9H2-2-113-4","TO"},
		{"ec9ecb91873241e5ab507130914da92b","9H2-2-3-2","TO"},
		{"74925419cf2f4745a63f7c0fc13adc49","9H2-2-3-4","TO"},
		{"7c18c4e1f77140af8588f6c7ab7a7194","9H5-3-2-2","TO"},
		{"ee4ffdcbf4144c77bd39a774b3a4942d","9H5-3-2-4","TO"},
		{"ba81da99e9dc4da49c1ff1d41bbb857d","9H8-14-300-2","TO"},
		{"522cd378973847a894e11278c4d882cc","9P5-14-47-2","TO"},
		{"2EC0BA7E1E5B48189CA1A274739A87FF","11계열_무장 장비","SubSystem"},
		{"4e6dc672a0a1405f9d0381bc6eae1ca9","11B29-3-25-2","TO"},
		{"5e6dc672a1b1405f9d1381bc6eaf1cb0","11B29-3-25-2S-2","TO"},
		{"9e0e6688d4c742eda449435601a8c923","11L1-2-16-2","TO"},
		{"9e3fec3209d443158e23c4bde852b468","11F12-1-2","TO"},
		{"c36baf597f3941488b0e7ca41c6ed869","11F12-1-4","TO"},
		{"00e27dc718ec4e8bb6095fb805f9ac3a","11F12-1-8","TO"},
		{"03669b6e490e4224a28792342d5aba01","11F13-14-1-2","TO"},
		{"58c86d22ca8d49c9b7913d964782d4d2","11F13-14-1-4","TO"},
		{"81aa888f94f14bf4b9cb070ef7b5dcfa","11F13-14-1-8","TO"},
		{"a110532a316645129239be1e0c5ea7cb","11F32-2-1-2","TO"},
		{"ef5f9f9008934804bf31441cbddc994b","11F32-2-1-4","TO"},
		{"a110532a316645129239be1e0c5ea7cc","11F32-2-1-2KS","TO"},
		{"ef5f9f9008934804bf31441cbddc994c","11F32-2-1-4KS","TO"},
		{"b78cf36e22674d70b46004ea67fe5fca","11F98-1-2","TO"},
		{"fd4ff39123664877bf3658a2befa5b1c","11F98-1-4","TO"},
		{"86c9537567be40daaf45f386a4a6e5f6","11F98-1-8","TO"},
		{"76a17e82fb9a4248be4740be1ebfa4a3","11W1-12-1-2","TO"},
		{"0cc6e1db8f9245da92ae9bcbcf5515e6","11W1-12-1-4","TO"},
		{"6307cae48076479699e3c9d634f74fbb","11W1-6-1-2","TO"},
		{"0741fc752af94918be264eab0f728de8","11W1-6-1-4","TO"},
		{"f53fc257a4b34f1094448c071b426d2d","11W1-7-1-2","TO"},
		{"189d1743dfcf41d09df160c70ed28acc","11W1-7-1-4","TO"},
		{"b6fc9a06791d4f6bb5a20fc7145b29f9","11W1-35-1-2","TO"},
		{"236a78f9077444e28babad4fb7fe0a04","11W1-35-1-4","TO"},
		{"9CF32A1BC4B346a2B452A60B75ABB773","12계열_항전 장비","SubSystem"},
		{"731FAB62CED246deBC3CF9E107133067","12P4-2APX118(V)-2","TO"},
		{"0A6844374E4647b89704457B3F2C029C","12P4-2APX118(V)-4","TO"},
		{"0ECBFE9A8539476980D158A51062F481","12P4-2APX118(V)-8","TO"},
		{"cfdd83b8b6914220b6d16b5a350453ff","12R2-2ARC232(V)-2","TO"},
		{"83065256aa11414fb4c8fa3c88854be6","12R2-2ARC232(V)-4","TO"},
		{"638de2b3369c424e973a9c8f34a19ea5","12R5-2ARN147(V)-2","TO"},
		{"3610a33334f34119bd3e97abb92f693c","12R5-2ARN147(V)-4","TO"},
		{"b73c7baf9d8142379203059768a19706","12R5-2ARN153(V)-2","TO"},
		{"8f1fe5e7c8134d0aa708d0d73ffbb283","12R5-2ARN153(V)-4","TO"},
		{"a43d3094ab2e4c0aa3329e1e810da1db","12R2-2ARC232-K01-2","TO"},
		{"b8010ef93eaa462dbad21e7bf9178e44","12R2-2ARC232-K01-4","TO"},
		{"5ed64dd510b44ac0926afe38f2ba34aa","12S2-4-1-2","TO"},
		{"a9d3bd275fbd40d9b83db5911c596dbe","12S2-4-1-4","TO"},
		{"cb68dedef6de4bb585f86abacb91ff64","12S2-4-1-8","TO"},
		{"9899192dbc8e43e9b2531300e3d93289","12S6-2-1-2","TO"},
		{"1abe6f035ef049b88bdcbde13e18986f","12S6-2-1-4","TO"},
		{"9899192dbc8e43e9b2531300e3d93280","12S6-2-1-2KS","TO"},
		{"1abe6f035ef049b88bdcbde13e18986g","12S6-2-1-4KS","TO"},
		{"5fe981383b694180a54e50cfb0e2aea1","12S6-2-2-2","TO"},
		{"7845a8fa388f4928934f370065c7d07a","12S6-2-2-4","TO"},
		{"45522a43dd9148f4974e39cc7b591cdd","13계열_항공 비품 및 비행중 공급 장비, 적하, 공중 투하 및 복구장비, 항공기 화재 감지 및 소화장비","SubSystem"},
		{"2a5e7fd6a2874a7681e53b550a0c5436","13A5-1-2","TO"},
		{"33435c9c49214bb4a823e225853b3d55","13A5-1-4","TO"},
		{"47c2c711df5f4d15a7671e2953efcac1","14계열_감속장치 및 개인생존장비","SubSystem"},
		{"f0c397479b744a839836f7384a6cd7f9","14D3-1-1","TO"},
		{"fc70d3f311714c59b64661b2e84eb9d8","14D3-1-4","TO"},
		{"4f5d6fc6a4194caeb6a4031c2851085c","14S1-1-1","TO"},
		{"ea1132395c8646aa93271450d8d4113d","14S1-1-4","TO"},
		{"83C4A240333A491c932DC7D5EFCC96E0","15계열_항공기 및 미사일 온도 제어, 가압, 공기 조화, 가열, 방빙 및 산소장비","SubSystem"},
		{"3812871EF3F94a029F78FB0708991256","15A5-2-1-2","TO"},
		{"AF6B5EF527D74d818F987E22EB819900","15A5-2-1-4","TO"},
		{"44a819d1be384fe0b74e1b878f3a98f9","15E3-2-1-2","TO"},
		{"1ba26659195c460f9b3f1981a815efb6","15E3-2-1-4","TO"},
		{"44a819d1be384fe0b74e1b878f3a98f0","15E3-2-1-2KS","TO"},
		{"1ba26659195c460f9b3f1981a815efb7","15E3-2-1-4KS","TO"},
		{"A5B82D6FC0DC4b39B9F860A93131BD27","16계열_항공 기계 장비","SubSystem"},
		{"974346F6865C42feA21398CBEAFB1691","16C1-27-50-2","TO"},
		{"6a0b39d80d194b05b790db16f68561cd","16W6-1-2","TO"},
		{"0a73faeb84e3416b9804920ca8e2eeea","16W6-1-4","TO"},
		{"4154E70577DC4b5e920FEF6503A31183","16W6-2-2","TO"},
		{"2fbdc491f94f4f40b5a9474b740dbee3","16W6-2-4","TO"},
		{"4c684e1a887e4ba3bd9ee3d50eefeef6","16W6-3-2","TO"},
		{"aa2754511f464a599380c4a96168e229","16W6-3-4","TO"},
		{"644d151a156241be9c448b7d2809e3b3","16W7-1-2","TO"},
		{"eb866c90bde94f27b99ccc3e4cbf3631","16W7-1-4","TO"},
		{"644d151a156241be9c448b7d2809e3b4","16W7-1-2KS","TO"},
		{"eb866c90bde94f27b99ccc3e4cbf3632","16W7-1-4KS","TO"},
		{"4952fc9d250247d2b0c6ab8c04b2f401","책자교범","TO"},
		{"3352fc9d250247d2b0c6ab8c04b2f401","TCTO","System"}
	};

	
	public static String toData1="		<table width='560' height='100%' border='0' cellspacing='0' cellpadding='0'  align='center' style='margin:0; padding:0;'>"
			+"		 <tr>"
			+"			 <td>"
			+"			 	<img src='"+projectName+"web/image/common/book_bg_top.png' />"
			+"			 </td>"
			+"		 </tr>"
			+"		<tr>"
			+"		<td>"
			+"			<table id='book_bg' border='0' cellspacing='0' cellpadding='0'  align='center'>"
			+"		    <tr>"
			+"				<td align='left'>기술도서</td>"
			+"				<td align='right'>K.T.O. 1T-50A-2-21JG-10-1</td>"
			+"			  </tr>"
			+"			  <tr>"
			+"				<td colspan='2' height='50'><h2>작업 지침서</h2></td>"
			+"			  </tr>"
			+"			  <tr>"
			+"				<td colspan='2' height='100'><h1>공기 조화 계통(압축)</h1></td>"
			+"			  </tr>"
			+"			  <tr>"
			+"				<td colspan='2' height='50'><h2>T-50/TA-50 항공기</h2></td>"
			+"			  </tr>"
			+"			  <tr>"
			+"				<td colspan='2'align='center' style='padding-top: 15px;'>"
			+"					<h4 style='text-decoration: underline;'>주 의 사 항</h4>"
			+"				</td>"
			+"			  </tr>"
			+"			  <tr>"
			+"				<td colspan='2'>"
			+"					<p>"
			+"						1. 이 교범은 군사목적 이외의 여하한 목적에도 사용을 금한다."
			+"						<br/> 2. 이 교범의 판권은 어떠한 개인이나 단체를 막론하고 침범할 수 없다."
			+"						<br/> 3. 이 교범은 복제, 복사를 할 수 없으나 단, 부대 교육 훈련 목적상 필요시에는 독립 전대/단장급 지휘관의 승인을 얻어 복사 사용할 수 있다."
			+"						<br/> 4. 이 교범을 국외로 반출할 때는 공군 참모총장의 사전 승인을 얻어야 한다."
			+"						<br/> 5. 이 교범의 관리는 공규2-72('65.5.10) 규정 및 교범관리 규정에 의한다."
			+"					</p>"
			+"				</td>"
			+"			  </tr>"
			+"			  <tr>"
			+"				<td colspan='2'align='center' style='padding-top: 15px;'>"
			+"					<h4 style='text-decoration: underline;'>공 지 사 항</h4>"
			+"				</td>"
			+"			  </tr>"
			+"			  <tr>"
			+"				<td colspan='2'>"
			+"					<p>"
			+"						1. 이 교범은 (주)포트데이타에서 초안을 작성하여 관계기관의 검토 및 승인을 받아 발간한 교범이다."
			+"						<br/> 2. 이 교범에서 수정된 내용은 '돋움체'로 중요한 내용은 '강조체'로 기술되어 있으며, 연구추진경과 및 주요변경사항은 부록 '2'를 참조한다."
			+"						<br/> 3. 이 교범은 접수 즉시 부대정비업무에 적용, 시행하여야 한다."
			+"						<br/> 4. 이 교범의 내용에 대한 수정 및 발전시켜야 할 사항은 본 교범에 첨부된 '규정•교범 수정 건의서' 양식에 의거 작전사령관 앞으로 제출한다."
			+"					</p>"
			+"				</td>"
			+"			  </tr>"
			+"			  <tr>"
			+"				<td colspan='2' height='50' valign='bottom'><h2>방 위 사 업 청</h2></td>"
			+"			  </tr>"
			+"			  <tr>"
			+"				<td colspan='2' height='50' valign='bottom' align='right'>"
			+"					2005.07.30"
			+"					<br/>변경판14 2012.02.29"
			+"				</td>"
			+"			  </tr>"
			+"		    </table>"
			+"		</td>"
			+"		</tr>"
			+"		<tr>"
			+"		<td><img src='"+projectName+"web/image/common/book_bg_bottom.png' />"
			+"		</td>"
			+"		</tr>"
			+"		</table>";
	
	public static String toData2= "<table width='425' border='0' cellspacing='0' cellpadding='0' bgcolor='#2d2d2d' align='center' style='margin-top: 55px;'>"
			+"<tr><td height='48'><img src='"+projectName+"web/image/ietm/popup/war_top_bg.png' /></td>"
			+"</tr><tr>"
			+"<td style='padding:20px; color:#FFF; line-height:20px;' align='left'>"
			+"본 기술도서에서는 잠재적인 위험물질의 사용에 대한 내용이 언급된다. 이러한 위험물질 사용이 인체에 직접적이고 심각한 위험을 초래할 경우, 본문의 해당 위치에 '경 고' 사항이 수록된다. 모든 위험물질에 대해서는 저장용기에 표시된 경고 및 주의사항을 읽고 준수하라. 위험물질에 대한 상세정보나 안전사항이 필요한 경우 물질안전 보건자료(MSDS)를 참조하고, 없으면 이에 상응한 안전 정보를 참조하라. 추가적인 정보가 필요한 경우에는 관리자나 안전 또는 보건 담당 부서에 문의하라. 정비 요원은 모든 안전수칙을 준수해야 한다. 이러한 안전수칙을 준수하지 않을 경우에는 폭발하거나 눈, 코, 피부, 기관지 및 폐의 손상 또는 사망을 초래할 수 있다."
			+"</td></tr>"
			+"<tr><td background='"+projectName+"web/image/ietm/popup/war_bottom_bg.png' height='54' align='center' valign='middle'>"
			+"<a href='#'  title='a'><img src='"+projectName+"web/image/ietm/popup/btn_close.png' onmouseover='this.src='"+projectName+"web/image/ietm/popup/btn_close_over.png'' onmouseout='this.src='"+projectName+"web/image/ietm/popup/btn_close.png'' alt='btn'/></a>"
			+"</td></tr>"
			+"</table>";
	
	
	public static String toData3= "<div class='ac-content'>"
					+"	<div class='ac-topic'><a href='#' class='ac-arrow' id='ac-title_1'  onclick='javascript:divOpen(1)'>&nbsp;</a>1. 목 적</div>"
					+"	<div class='ac-desc' id='ac-div_1' style='display:none;'>"
					+"		본 기술도서에는 T-50/TA-50 공기 조화 계통에 대한 부대급 정비 지침이 수록되어 있다"
					+"	</div>"
					+"</div>"
					+"<div class='ac-content'>"
					+"	<div class='ac-topic'><a href='#' class='ac-arrow' id='ac-title_2' onclick='javascript:divOpen(2)'>&nbsp;</a><a href='#' >2. 범 위</a></div>"
					+"	<div class='ac-task' id='ac-div_2' style='display:none;'>"
					+"		<p class='step1'>가. 본 기술도서에는 T-50/TA-50 공기 조화 계통의 구성품에 대한 모든 정비행위가 단계적으로 수록되어 있다. 공기 조화 계통 작업 지침서에는 정비 기능/정비 절차 순으로 수록되어 있으며 정비 절차 내 각 정비 항목에 해당하는 도해는 정비 절차와 함께 수록되어 있다.</p>"
					+"		<p class='step1'>나. 본 기술도서의 도해는 작업이 수행될 구성품의 위치와 장착된 상태를 보여주며, 도해의 구성품에는 정비 절차 내 해당 정비 항목 번호와 동일한 번호가 부여된다(그림 1 참조). 도해에 있는 구성품이 하나 이상의 정비 항목에 해당되는 경우, 도해에 해당 정비 항목 번호를 모두 표시한다(그림 1의 4, 5번 참조). 정비 절차 내 해당 정비 항목이 “일반 정비” 또는 다른 정비 기능에 관련될 때는 해당 정비 항목 번호를 도해상에 표시하지 않는다. 정비사는 “일반 정비”에 대한 정보를 얻기 위해서는 반드시 “제Ⅰ장 일반 정비 절차”를 참조한다. 도해상의 “일반 형상”이란 용어는 2개 이상의 형상이 존재하지만, 하나의 형상만을 대표적으로 표현했다는 의미이다</p>"
					+"		<p class='step1'>다. 해당 정비 절차가 여러 장의 같은 도해를 활용하여 기술할 때, 정비 절차 내 정비 항목 번호는 이전 장에서 후속 장까지 계속 이어서 부여한다.</p>"
					+"		<p class='step1'>라. 본 기술도서 내에서 장탈 및 장착, 개폐가 필요한 모든 점검창, 점검판의 도해는 “제Ⅰ장 일반 정비 절차”에 수록되어 있다.</p>"
					+"		<p class='step1'>마. 본 기술도서에 적용되는 모든 조종석 형상과 콘솔 및 계기 패널에 있는 모든 구성품들은 “제Ⅰ장 일반 정비 절차”에 수록되어 있다.</p>"
					+"		<p class='step1'>바. 본 기술도서에 수록된 정비 기능을 수행하기 위하여 외부 전원, 냉각 공기, 지상 서비스 블리드 에어 및 유압이 필요하다면, 해당 지원 장비를 연결 및 분리하는 도해와 절차는 “제Ⅰ장 일반 정비 절차”에 수록되어 있다.</p>"
					+"		<p class='step1'>사. 여러 가지 정비 기능을 수행하기 위한 준비 사항과 후속 정비 앞 부분의 괄호 안에 명시된 숫자들은 해당 정비 절차에 대한 항 번호의 마지막 숫자를 의미한다. 예를 들어 준비 사항 및 후속 정비 앞 부분에 명시된 괄호 안의 숫자(1,10)는 정비 기능에 포함된 해당 정비 절차의 항, 즉 X.X.1 항과 X.X.10 항의 마지막 숫자 1, 10을 나타내며, 이는 정비 기능의 해당 정비 절차에만 적용됨을 의미하는 것이다. 준비 사항에 정비 소요 인원이 2명 이상일 경우, 정비 항목의 항 번호 뒤에 문자를 부여하여 그 정비 항목을 수행할 정비 요원 각각의 임무를 표시한다.</p>"
					+"		<p class='step1'>아. 정비 기능은 하나 이상의 정비 절차로 구성될 수 있다. 일반적으로 정비 기능을 충족시키기 위한 모든 요소(준비 사항, 정비 절차, 후속 정비)들을 반드시 수행해야 한다. 또한 정비 기능의 일부 정비 절차만을 수행하는 경우에도 준비 사항과 후속 정비에 기호로 표시된 해당 절차를 반드시 수행해야 한다. 정비 항목에 고장 탐구 의미로 관련 결과치를 수록하는 경우에는 괄호 안에 결함 코드를 추가한다. 이 결함 코드는 고장 탐구 교범(FIM)의 결함 코드와 동일하다.</p>"
					+"	</div>"
					+"</div>"
					+"<div class='ac-content'>"
					+"	<div class='ac-topic'><a href='#' class='ac-arrow' id='ac-title_3' onclick='javascript:divOpen(3)'>&nbsp;</a><a href='#' >3. 표준 및 비표준 약어</a></div>"
					+"	<div class='ac-desc' id='ac-div_3' style='display:none;'>"
					+"		<table border='0' cellspacing='1' cellpadding='0' class='in_table' bgcolor='#83a9c'>"
					+"		<colgroup>"
					+"			<col width='15%' />"
					+"			<col width='85%' />"
					+"		</colgroup>"
					+"		<tr>"
					+"			<th align='center'>약어</th>"
					+"			<th align='center'>정의</th>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>AFT</td>"
					+"			<td>After(후방)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>BIT</td>"
					+"			<td>Built In Test(자체 진단 시험)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>ECS</td>"
					+"			<td>Environmental Control System(환경 제어 계통)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>FI</td>"
					+"			<td>Fault Isolation(고장 탐구)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>FIM</td>"
					+"			<td>Fault Isolation Manual(고장 탐구 교범)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>FR</td>"
					+"			<td>Fault Reporting(결함 보고)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>GS</td>"
					+"			<td>General System(일반 계통)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>HP</td>"
					+"			<td>High Pressure(고압)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>IBIT</td>"
					+"			<td>Initiated Built In Test(초기 자체 진단 시험)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>JG</td>"
					+"			<td>Job Guide(작업 지침서)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>L/H</td>"
					+"			<td>Left Hand(좌측)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>LP</td>"
					+"			<td>Low Pressure(저압)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>MLG</td>"
					+"			<td>Main Landing Gear(주 착륙 기어)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>P/N</td>"
					+"			<td>Part Number(부품 번호)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>PRSOV</td>"
					+"			<td>Pressure Regulating and Shut-Off Valve(압력 조절 및 차단 밸브)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>RDN</td>"
					+"			<td>Reference Designation(Designator) Number(참조 지시 번호)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>R/H</td>"
					+"			<td>Right Hand(우측)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>RITB</td>"
					+"			<td>Regulated Integrated Terminal Block(조절식 통합 터미널 블록)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>TCTO</td>"
					+"			<td>Time Compliance Technical Order(시한성 기술 지시)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>TO</td>"
					+"			<td>Technical Order(기술 지시)</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>TODO</td>"
					+"			<td>Technical Order Distribution Office(중앙 기술도서 관리소)</td>"
					+"		</tr>"
					+"		</table>"
					+"	</div>"
					+"</div>"
					+"<div class='ac-content'>"
					+"	<div class='ac-topic'><a href='#' class='ac-arrow' id='ac-title_4' onclick='javascript:divOpen(4)'>&nbsp;</a><a href='#'>4. 용어 정의</a></div>"
					+"	<div class='ac-desc' id='ac-div_4' style='display:none;'>"
					+"		본 기술도서에 사용되는 용어의 정의는 다음과 같다."
					+"		<table border='0' cellspacing='1' cellpadding='0' class='in_table' bgcolor='#83a9c'>"
					+"		<colgroup>"
					+"			<col width='15%' />"
					+"			<col width='85%' />"
					+"		</colgroup>"
					+"		<tr>"
					+"			<td>가. 정비 기능</td>"
					+"			<td>계통 또는 한 구성품이 작동 준비상태가 되도록 수행하는 한가지 이상의 정비 절차. 일반적으로 준비 사항, 정비 절차, 후속 정비로 구성된다.</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>나. 정비 절차</td>"
					+"			<td>처음부터 끝까지 단계적 정비 행위를 명시한 정비 항목들의 묶음.</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td>다. 정비 항목</td>"
					+"			<td>하나의 정비 행위(예 : 전기 커넥터를 연결한다.)</td>"
					+"		</tr>"
					+"		</table>"
					+"	</div>"
					+"</div>"
					+"<div class='ac-content'>"
					+"	<div class='ac-topic'><a href='#' class='ac-arrow' id='ac-title_5' onclick='javascript:divOpen(5)'>&nbsp;</a><a href='#'>5. 경고, 주의 및 주기</a></div>"
					+"	<div class='ac-desc' id='ac-div_5' style='display:none;'>"
					+"		본 기술도서에 사용된 경고, 주의 및 주기는 다음과 같이 정의된다."
					+"		<table border='0' cellspacing='1' cellpadding='0' class='in_table' bgcolor='#83a9c'>"
					+"		<colgroup>"
					+"			<col width='15%' />"
					+"			<col width='85%' />"
					+"		</colgroup>"
					+"		<tr>"
					+"			<td align='center'>경고</td>"
					+"			<td>엄격하게 준수하지 않으면 인명의 상해나 사망을 초래할 수 있는 작동 또는 정비절차, 훈련, 설명 및 조건.</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td align='center'>주의</td>"
					+"			<td>엄격하게 준수하지 않으면 장비의 손상이나 파손 또는 임무 효율성의 손실을 초래할 수 있는 작동 또는 정비절차, 훈련, 설명 및 조건.</td>"
					+"		</tr>"
					+"		<tr>"
					+"			<td align='center'>주기</td>"
					+"			<td>강조해야 하는 필수적인 작동 또는 정비절차, 설명 및 조건.</td>"
					+"		</tr>"
					+"		</table>"
					+"	</div>"
					+"</div>"
					+"<div class='ac-content'>"
					+"	<div class='ac-topic'><a href='#' class='ac-arrow' id='ac-title_6' onclick='javascript:divOpen(6)'>&nbsp;</a><a href='#'>6. 항공기 및 조종석 식별 부호</a></div>"
					+"	<div class='ac-desc' id='ac-div_6' style='display:none;'>"
					+"		<p>"
					+"			본 기술도서 내 본문 또는 도해에서는 박스 문자 또는 부호를 이용하여 특정 항공기 버전 및/또는 조종석에 적용 가능한 시스템 또는 구성품의 유효성(Effectivity)을 식별 표시하고 있다. 이러한 부호 및 명칭은 다음과 같다. "
					+"			<br/><br/>부호 없음 - T-50 및 T-50B, TA-50 항공기"
					+"			<br/>T-50 항공기"
					+"			<br/>T-50B 항공기"
					+"			<br/>TA-50 항공기"
					+"			<br/>T-50 항공기, 전방석"
					+"			<br/>T-50 항공기, 후방석"
					+"			<br/>TA-50 항공기, 전방석"
					+"			<br/>TA-50 항공기, 후방석"
					+"		</p>"
					+"	</div>"
					+"</div>"
					+"<div class='ac-content'>"
					+"	<div class='ac-topic'><a href='#' class='ac-arrow' id='ac-title_7' onclick='javascript:divOpen(7)'>&nbsp;</a><a href='#'>7. 항공기 유효성 기호 및 소급개조(RETROFIT) 정보</a></div>"
					+"	<div class='ac-desc' id='ac-div_7' style='display:none;'>"
					+"		항공기 유효성 기호는 특정 항공기 형상에 따라 필요한 절차와 적용 가능한 기술회보(SL)/시한성기술지시(TCTO)의 유효 정보를 식별하는데 사용된다. 두 항공기 일련번호 사이의 화살표는 두 번호 사이에 연속적으로 일련번호가 부여된 모든 항공기를 의미한다. 일련번호 끝에 사용된 화살표는 일련번호 이후의 모든 항공기를 의미한다. 본 기술도서에서는 특정 항공기 및 기술회보(SL)/시한성기술지시(TCTO)에 의거하여 개조되는 항공기를 구분"
					+"		<br/><br/>설명하기 위해 하얀색 숫자(1)가 포함된 흑색 TV 화면 기호( )가 사용된다. 개조 항공기에 관한 정보는 해당 유효성 기호를 이용하여 식별한다. 비개조 항공기에 관한 정보는 해당 유효성 기호 앞에  를 표기하여 식별한다. 예를 들어,  은 특정 기술회보(SL)/시한성기술지시(TCTO)에 따라 개조되지 않는 항공기에만 적용하는 정보를 나타낸다."
					+"	</div>"
					+"</div>"
					+"<div class='ac-content'>"
					+"	<div class='ac-topic'><a href='#' class='ac-arrow' id='ac-title_8' onclick='javascript:divOpen(8)'>&nbsp;</a><a href='#'>8. 참조 지시 번호(RDN) 사용</a></div>"
					+"	<div class='ac-desc' id='ac-div_8' style='display:none;'>"
					+"		참조 지시 번호(RDN)는 숫자 및 영문 알파벳으로 구성되고, 항공기 내에서 특정 부품의 해당 계통 및 위치를 구별하기 위한 수단으로 사용된다. 어떤 경우에는 2개의 RDN이 동일 부품에 부여된 것이 있다. 이러한 경우 항공기 부품에 표시된 RDN이 앞에 명시되고, 항공기 부품에 표시되지 않은 다른 RDN이 그 뒤에 괄호 안에 표시된다. 이중 RDN은 단지 고장 탐구 교범(FIM)의 계통도에만 적용된다. 본 기술도서에서는 작업 지침서(JG), 결함 보고(FR), 고장 탐구(FI) 및 일반 계통(GS) 기술도서에 있는 RDN과 항공기 부품에 표시된 RDN에 대해 상호 참조가 용이하도록 하기 위해 이중 RDN을 사용할 수 있다."
					+"	</div>"
					+"</div>"
					+"<div class='ac-content'>"
					+"	<div class='ac-topic'><a href='#' class='ac-arrow' id='ac-title_9' onclick='javascript:divOpen(9)'>&nbsp;</a><a href='#'>9. 변경 의뢰</a></div>"
					+"	<div class='ac-desc' id='ac-div_9' style='display:none;'>"
					+"		본 기술도서와 관련된 변경 의뢰는 “공규 5-52 항공기술도서관리”에 따라 아래 주소로 제출한다."
					+"		<br/>(우편번호 701-799 대구광역시 동구 검사동 사서함 304-170호 중앙 기술도서 관리소)"
					+"	</div>"
					+"</div>"
					+"<div class='ac-content'>"
					+"	<div class='ac-topic'><a href='#' class='ac-arrow' id='ac-title_10' onclick='javascript:divOpen(10)'>&nbsp;</a><a href='#'>10. 시한성 기술 지시(TCTO) 기록</a></div>"
					+"	<div class='ac-desc' id='ac-div_10' style='display:none;'>"
					+"		다음은 본 기술도서 내의 장비에 적용 가능한 시한성 기술지시(TCTO)를 수록한 것이다; 적용 사항 없음."
					+"	</div>"
					+"</div>";
	
 
	public static String toData4= "<div class='ac-content'>"
			+"	<div class='ac-topic'><a href='#' class='ac-arrow-open' id='ac-title_1'  onclick='javascript:divOpen(1)'>&nbsp;</a><a href='#'>1. 목 적</a></div>"
			+"	<div class='ac-desc' id='ac-div_1'>"
			+"		본 기술도서에는 T-50/TA-50 공기 조화 계통에 대한 부대급 정비 지침이 수록되어 있다."
			+"	</div>"
			+"</div>";
	
	public static String toData4_1= "1. 목 적";
	public static String toData4_2=	"<p class='dott'>본 기술도서에는 T-50/TA-50 공기 조화 계통에 대한 부대급 정비 지침이 수록되어 있다.</p>";
	
	public static String toData5= "<div class='ac-content'>"
			+"	<div class='ac-topic'><a href='#' class='ac-arrow-open' id='ac-title_2' onclick='javascript:divOpen(2)'>&nbsp;</a><a href='#' >2. 범 위</a></div>"
			+"	<div class='ac-task' id='ac-div_2' >"
			+"		<p class='step1'>가. 본 기술도서에는 T-50/TA-50 공기 조화 계통의 구성품에 대한 모든 정비행위가 단계적으로 수록되어 있다. 공기 조화 계통 작업 지침서에는 정비 기능/정비 절차 순으로 수록되어 있으며 정비 절차 내 각 정비 항목에 해당하는 도해는 정비 절차와 함께 수록되어 있다.</p>"
			+"		<p class='step1'>나. 본 기술도서의 도해는 작업이 수행될 구성품의 위치와 장착된 상태를 보여주며, 도해의 구성품에는 정비 절차 내 해당 정비 항목 번호와 동일한 번호가 부여된다(그림 1 참조). 도해에 있는 구성품이 하나 이상의 정비 항목에 해당되는 경우, 도해에 해당 정비 항목 번호를 모두 표시한다(그림 1의 4, 5번 참조). 정비 절차 내 해당 정비 항목이 “일반 정비” 또는 다른 정비 기능에 관련될 때는 해당 정비 항목 번호를 도해상에 표시하지 않는다. 정비사는 “일반 정비”에 대한 정보를 얻기 위해서는 반드시 “제Ⅰ장 일반 정비 절차”를 참조한다. 도해상의 “일반 형상”이란 용어는 2개 이상의 형상이 존재하지만, 하나의 형상만을 대표적으로 표현했다는 의미이다</p>"
			+"		<p class='step1'>다. 해당 정비 절차가 여러 장의 같은 도해를 활용하여 기술할 때, 정비 절차 내 정비 항목 번호는 이전 장에서 후속 장까지 계속 이어서 부여한다.</p>"
			+"		<p class='step1'>라. 본 기술도서 내에서 장탈 및 장착, 개폐가 필요한 모든 점검창, 점검판의 도해는 “제Ⅰ장 일반 정비 절차”에 수록되어 있다.</p>"
			+"		<p class='step1'>마. 본 기술도서에 적용되는 모든 조종석 형상과 콘솔 및 계기 패널에 있는 모든 구성품들은 “제Ⅰ장 일반 정비 절차”에 수록되어 있다.</p>"
			+"		<p class='step1'>바. 본 기술도서에 수록된 정비 기능을 수행하기 위하여 외부 전원, 냉각 공기, 지상 서비스 블리드 에어 및 유압이 필요하다면, 해당 지원 장비를 연결 및 분리하는 도해와 절차는 “제Ⅰ장 일반 정비 절차”에 수록되어 있다.</p>"
			+"		<p class='step1'>사. 여러 가지 정비 기능을 수행하기 위한 준비 사항과 후속 정비 앞 부분의 괄호 안에 명시된 숫자들은 해당 정비 절차에 대한 항 번호의 마지막 숫자를 의미한다. 예를 들어 준비 사항 및 후속 정비 앞 부분에 명시된 괄호 안의 숫자(1,10)는 정비 기능에 포함된 해당 정비 절차의 항, 즉 X.X.1 항과 X.X.10 항의 마지막 숫자 1, 10을 나타내며, 이는 정비 기능의 해당 정비 절차에만 적용됨을 의미하는 것이다. 준비 사항에 정비 소요 인원이 2명 이상일 경우, 정비 항목의 항 번호 뒤에 문자를 부여하여 그 정비 항목을 수행할 정비 요원 각각의 임무를 표시한다.</p>"
			+"		<p class='step1'>아. 정비 기능은 하나 이상의 정비 절차로 구성될 수 있다. 일반적으로 정비 기능을 충족시키기 위한 모든 요소(준비 사항, 정비 절차, 후속 정비)들을 반드시 수행해야 한다. 또한 정비 기능의 일부 정비 절차만을 수행하는 경우에도 준비 사항과 후속 정비에 기호로 표시된 해당 절차를 반드시 수행해야 한다. 정비 항목에 고장 탐구 의미로 관련 결과치를 수록하는 경우에는 괄호 안에 결함 코드를 추가한다. 이 결함 코드는 고장 탐구 교범(FIM)의 결함 코드와 동일하다.</p>"
			+"		<table class='in_table'>"
			+"		<colgroup>"
			+"			<col width='15%' />"
			+"			<col width='85%' />"
			+"		</colgroup>"
			+"		<tr>"
			+"			<td align='center'>경고</td>"
			+"			<td>엄격하게 준수하지 않으면 인명의 상해나 사망을 초래할 수 있는 작동 또는 정비절차, 훈련, 설명 및 조건.</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td align='center'>주의</td>"
			+"			<td>엄격하게 준수하지 않으면 장비의 손상이나 파손 또는 임무 효율성의 손실을 초래할 수 있는 작동 또는 정비절차, 훈련, 설명 및 조건.</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td align='center'>주기</td>"
			+"			<td>강조해야 하는 필수적인 작동 또는 정비절차, 설명 및 조건.</td>"
			+"		</tr>"
			+"		</table>"
			+"	</div>"
			+"	</div>"
			+"</div>";
	
	
	public static String toData6= "<div class='ac-content'>"
			+"	<div class='ac-topic'><a href='#' class='ac-arrow-open' id='ac-title_3' onclick='javascript:divOpen(3)'>&nbsp;</a><a href='#' >3. 표준 및 비표준 약어</a></div>"
			+"	<div class='ac-desc' id='ac-div_3' >"
			+"		<table class='in_table'>"
			+"		<colgroup>"
			+"			<col width='15%' />"
			+"			<col width='85%' />"
			+"		</colgroup>"
			+"		<tr>"
			+"			<th align='center'>약어</th>"
			+"			<th align='center'>정의</th>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>AFT</td>"
			+"			<td>After(후방)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>BIT</td>"
			+"			<td>Built In Test(자체 진단 시험)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>ECS</td>"
			+"			<td>Environmental Control System(환경 제어 계통)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>FI</td>"
			+"			<td>Fault Isolation(고장 탐구)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>FIM</td>"
			+"			<td>Fault Isolation Manual(고장 탐구 교범)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>FR</td>"
			+"			<td>Fault Reporting(결함 보고)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>GS</td>"
			+"			<td>General System(일반 계통)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>HP</td>"
			+"			<td>High Pressure(고압)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>IBIT</td>"
			+"			<td>Initiated Built In Test(초기 자체 진단 시험)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>JG</td>"
			+"			<td>Job Guide(작업 지침서)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>L/H</td>"
			+"			<td>Left Hand(좌측)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>LP</td>"
			+"			<td>Low Pressure(저압)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>MLG</td>"
			+"			<td>Main Landing Gear(주 착륙 기어)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>P/N</td>"
			+"			<td>Part Number(부품 번호)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>PRSOV</td>"
			+"			<td>Pressure Regulating and Shut-Off Valve(압력 조절 및 차단 밸브)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>RDN</td>"
			+"			<td>Reference Designation(Designator) Number(참조 지시 번호)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>R/H</td>"
			+"			<td>Right Hand(우측)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>RITB</td>"
			+"			<td>Regulated Integrated Terminal Block(조절식 통합 터미널 블록)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>TCTO</td>"
			+"			<td>Time Compliance Technical Order(시한성 기술 지시)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>TO</td>"
			+"			<td>Technical Order(기술 지시)</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>TODO</td>"
			+"			<td>Technical Order Distribution Office(중앙 기술도서 관리소)</td>"
			+"		</tr>"
			+"		</table>"
			+"	</div>"
			+"</div>";

	
	public static String toData7= "<div class='ac-content'>"
			+"	<div class='ac-topic'><a href='#' class='ac-arrow-open' id='ac-title_4' onclick='javascript:divOpen(4)'>&nbsp;</a><a href='#'>4. 용어 정의</a></div>"
			+"	<div class='ac-desc' id='ac-div_4' >"
			+"		본 기술도서에 사용되는 용어의 정의는 다음과 같다."
			+"		<table class='in_table'>"
			+"		<colgroup>"
			+"			<col width='15%' />"
			+"			<col width='85%' />"
			+"		</colgroup>"
			+"		<tr>"
			+"			<td>가. 정비 기능</td>"
			+"			<td>계통 또는 한 구성품이 작동 준비상태가 되도록 수행하는 한가지 이상의 정비 절차. 일반적으로 준비 사항, 정비 절차, 후속 정비로 구성된다.</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>나. 정비 절차</td>"
			+"			<td>처음부터 끝까지 단계적 정비 행위를 명시한 정비 항목들의 묶음.</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td>다. 정비 항목</td>"
			+"			<td>하나의 정비 행위(예 : 전기 커넥터를 연결한다.)</td>"
			+"		</tr>"
			+"		</table>"
			+"	</div>"
			+"</div>";
	
			
	public static String toData8="<div class='ac-content'>"
			+"	<div class='ac-topic'><a href='#' class='ac-arrow-open' id='ac-title_5' onclick='javascript:divOpen(5)'>&nbsp;</a><a href='#'>5. 경고, 주의 및 주기</a></div>"
			+"	<div class='ac-desc' id='ac-div_5' >"
			+"		본 기술도서에 사용된 경고, 주의 및 주기는 다음과 같이 정의된다."
			+"		<table class='in_table'>"
			+"		<colgroup>"
			+"			<col width='15%' />"
			+"			<col width='85%' />"
			+"		</colgroup>"
			+"		<tr>"
			+"			<td align='center'>경고</td>"
			+"			<td>엄격하게 준수하지 않으면 인명의 상해나 사망을 초래할 수 있는 작동 또는 정비절차, 훈련, 설명 및 조건.</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td align='center'>주의</td>"
			+"			<td>엄격하게 준수하지 않으면 장비의 손상이나 파손 또는 임무 효율성의 손실을 초래할 수 있는 작동 또는 정비절차, 훈련, 설명 및 조건.</td>"
			+"		</tr>"
			+"		<tr>"
			+"			<td align='center'>주기</td>"
			+"			<td>강조해야 하는 필수적인 작동 또는 정비절차, 설명 및 조건.</td>"
			+"		</tr>"
			+"		</table>"
			+"	</div>"
			+"</div>";
	
	
	public static String toData9="<div class='ac-content'>"
			+"	<div class='ac-topic'><a href='#' class='ac-arrow-open' id='ac-title_6' onclick='javascript:divOpen(6)'>&nbsp;</a><a href='#'>6. 항공기 및 조종석 식별 부호</a></div>"
			+"	<div class='ac-desc' id='ac-div_6' >"
			+"		본 기술도서 내 본문 또는 도해에서는 박스 문자 또는 부호를 이용하여 특정 항공기 버전 및/또는 조종석에 적용 가능한 시스템 또는 구성품의 유효성(Effectivity)을 식별 표시하고 있다. 이러한 부호 및 명칭은 다음과 같다. "
			+"		<br/><br/>부호 없음 - T-50 및 T-50B, TA-50 항공기"
			+"		<br/>T-50 항공기"
			+"		<br/>T-50B 항공기"
			+"		<br/>TA-50 항공기"
			+"		<br/>T-50 항공기, 전방석"
			+"		<br/>T-50 항공기, 후방석"
			+"		<br/>TA-50 항공기, 전방석"
			+"		<br/>TA-50 항공기, 후방석"
			+"	</div>"
			+"</div>";
	
	
	public static String toData10="<div class='ac-content'>"
			+"	<div class='ac-topic'><a href='#' class='ac-arrow-open' id='ac-title_7' onclick='javascript:divOpen(7)'>&nbsp;</a><a href='#'>7. 항공기 유효성 기호 및 소급개조(RETROFIT) 정보</a></div>"
			+"	<div class='ac-desc' id='ac-div_7' >"
			+"		항공기 유효성 기호는 특정 항공기 형상에 따라 필요한 절차와 적용 가능한 기술회보(SL)/시한성기술지시(TCTO)의 유효 정보를 식별하는데 사용된다. 두 항공기 일련번호 사이의 화살표는 두 번호 사이에 연속적으로 일련번호가 부여된 모든 항공기를 의미한다. 일련번호 끝에 사용된 화살표는 일련번호 이후의 모든 항공기를 의미한다. 본 기술도서에서는 특정 항공기 및 기술회보(SL)/시한성기술지시(TCTO)에 의거하여 개조되는 항공기를 구분"
			+"		<br/><br/>설명하기 위해 하얀색 숫자(1)가 포함된 흑색 TV 화면 기호( )가 사용된다. 개조 항공기에 관한 정보는 해당 유효성 기호를 이용하여 식별한다. 비개조 항공기에 관한 정보는 해당 유효성 기호 앞에  를 표기하여 식별한다. 예를 들어,  은 특정 기술회보(SL)/시한성기술지시(TCTO)에 따라 개조되지 않는 항공기에만 적용하는 정보를 나타낸다."
			+"	</div>"
			+"</div>";
	
	
	public static String toData11="<div class='ac-content'>"
			+"	<div class='ac-topic'><a href='#' class='ac-arrow-open' id='ac-title_8' onclick='javascript:divOpen(8)'>&nbsp;</a><a href='#'>8. 참조 지시 번호(RDN) 사용</a></div>"
			+"	<div class='ac-desc' id='ac-div_8' >"
			+"		참조 지시 번호(RDN)는 숫자 및 영문 알파벳으로 구성되고, 항공기 내에서 특정 부품의 해당 계통 및 위치를 구별하기 위한 수단으로 사용된다. 어떤 경우에는 2개의 RDN이 동일 부품에 부여된 것이 있다. 이러한 경우 항공기 부품에 표시된 RDN이 앞에 명시되고, 항공기 부품에 표시되지 않은 다른 RDN이 그 뒤에 괄호 안에 표시된다. 이중 RDN은 단지 고장 탐구 교범(FIM)의 계통도에만 적용된다. 본 기술도서에서는 작업 지침서(JG), 결함 보고(FR), 고장 탐구(FI) 및 일반 계통(GS) 기술도서에 있는 RDN과 항공기 부품에 표시된 RDN에 대해 상호 참조가 용이하도록 하기 위해 이중 RDN을 사용할 수 있다."
			+"	</div>"
			+"</div>";
	
	
	public static String toData12="<div class='ac-content'>"
			+"	<div class='ac-topic'><a href='#' class='ac-arrow-open' id='ac-title_9' onclick='javascript:divOpen(9)'>&nbsp;</a><a href='#'>9. 변경 의뢰</a></div>"
			+"	<div class='ac-desc' id='ac-div_9' >"
			+"		본 기술도서와 관련된 변경 의뢰는 “공규 5-52 항공기술도서관리”에 따라 아래 주소로 제출한다."
			+"		<br/>(우편번호 701-799 대구광역시 동구 검사동 사서함 304-170호 중앙 기술도서 관리소)"
			+"	</div>"
			+"</div>";
	
	
	public static String toData13="<div class='ac-content'>"
			+"	<div class='ac-topic'><a href='#' class='ac-arrow-open' id='ac-title_10' onclick='javascript:divOpen(10)'>&nbsp;</a><a href='#'>10. 시한성 기술 지시(TCTO) 기록</a></div>"
			+"	<div class='ac-desc' id='ac-div_10' >"
			+"		다음은 본 기술도서 내의 장비에 적용 가능한 시한성 기술지시(TCTO)를 수록한 것이다; 적용 사항 없음."
			+"	</div>"
			+"</div>";
	
	
	public static String toData14="<div class='ac-content'>"
			+"	<p class='ac-chapter'>제 Ⅰ장 일반 정비 절차</p>"
			+"	<div class='ac-desc'>" 
			+"		<div class='ac-input'>준비사항</div>"
			+"		<ul class='input-item'>"
			+"			<li>적용범위 : T-50/TA-50 항공기</li>"
			+"			<li>요구조건"
			+"				<ul>"
			+"					<li>● 장비를 위한 항공기 안전상태 확인<a href='#'>(JG10-30-01).</a>" 
			+"					<div class='ac-alert-caution'>"
			+"						<div>									"
			+"							<p>장비 손상을 방지하기 위하여, 비행제어 컴퓨터(FLCC) 장탈시 배선 하네스가 손상되지 않도록 주의하라.</p>"
			+"						</div>"
			+"					</div>"
			+"					</li>" 
			+"					<li>● 정검창 1101 개방<a href='#'>(JG52-00-01).</a>" 
			+"						<table class='in_table'>"
			+"							<colgroup>"
			+"								<col width='50%'/>"
			+"								<col width='50%'/>"
			+"							</colgroup>"
			+"							<tr>"
			+"								<td class='caption' colspan='2'>항목</td>"
			+"							</tr>"
			+"							<tr>"
			+"								<td>(2) K.T.O 1T-50A-2-00GV-00-1</td>"
			+"								<td>(2) K.T.O 1T-50A-2-21GS-00-1</td>"
			+"							</tr>"
			+"							<tr>"
			+"								<td>결합면의 전기적 본딩(Electrical Bonding) 수행</td>"
			+"								<td>덕트 오리피스 장탈착 및 점검</td>"
			+"							</tr>"
			+"							<tr>"
			+"								<td class='caption' colspan='2'>결합면의 전기적 본딩(Electrical Bonding) 수행</td>"
			+"							</tr>"
			+"						</table>"
			+"						<table class='in_table'>"
			+"							<colgroup>"
			+"								<col width='50%'/>"
			+"								<col width='50%'/>"
			+"							</colgroup>"
			+"							<tr>"
			+"								<th>항목</th>"
			+"								<th>목적</th>"
			+"							</tr>"
			+"							<tr>"
			+"								<td>(2) K.T.O 1T-50A-2-00GV-00-1</td>"
			+"								<td>(2) K.T.O 1T-50A-2-21GS-00-1</td>"
			+"							</tr>"
			+"							<tr>"
			+"								<td>결합면의 전기적 본딩(Electrical Bonding) 수행</td>"
			+"								<td>덕트 오리피스 장탈착 및 점검</td>"
			+"							</tr>"
			+"						</table>"
			+"					</li>" 
			+"					<li>● 정검창 1202 개방<a href='#'>(JG52-00-01).</a></li>" 
			+"					<li>● 비행 제어 컴퓨터(FLCC) 장탈<a href='#'>(JG52-00-01).</a></li>" 
			+"				</ul>"
			+"			</li>"
			+"			<li>소요인원 : 1명"
			+"				<ul>"
			+"					<li>● 장비를 위한 항공기 안전상태 확인<a href='#'>(JG10-30-01).</a></li>"  
			+"				</ul>"
			+"			</li>"
			+"			<li>지원장비 :" 
			+"				<ul>"
			+"					<li>● (2)토크 렌치(Torque Wrench), 토크범위:0~35 in lb</li>"
			+"				</ul>"
			+"			</li>"
			+"			<li>필수교환 품목 및 소모성 물자 :<span>해당 사항 없음.</span></li>" 
			+"			<li>안전요건 :" 
			+"				<div class='ac-alert-warning'>"
			+"					<div>									"
			+"						<p class='alert-step1'>● 인명 상해를 방지하기 위하여, 비행 제어 컴퓨터 장탈시 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라 장탈시 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라장비 구조물에 손이 접근하지 않도록 주의하라 장탈시 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라.</p>"
			+"						<p class='alert-step2'>● 인명 상해를 방지하기 위하여, 비행 제어 컴퓨터 장탈시 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라.</p>"
			+"					</div>"
			+"				</div>"
			+"			</li>"
			+"			<li>기타요건 :" 
			+"				<table class='in_table'>"
			+"					<colgroup>"
			+"						<col width='50%'/>"
			+"						<col width='50%'/>"
			+"					</colgroup>"
			+"					<tr>"
			+"						<th>항목</th>"
			+"						<th>목적</th>"
			+"					</tr>"
			+"					<tr>"
			+"						<td>(2) K.T.O 1T-50A-2-00GV-00-1</td>"
			+"						<td>(2) K.T.O 1T-50A-2-21GS-00-1</td>"
			+"					</tr>"
			+"					<tr>"
			+"						<td>결합면의 전기적 본딩(Electrical Bonding) 수행</td>"
			+"						<td>덕트 오리피스 장탈착 및 점검</td>"
			+"					</tr>"
			+"				</table>"
			+"			</li>"
			+"		</ul>"
			+"	</div>"
			+"	<div class='ac-desc'>"
			+"		<div class='ac-input'>정비절차</div>"
			+"		<div href='#' class='ac-sub-topic'>3.1.1 비행 제어 컴퓨터(FLCC) 장탈(27-00-50)</div>"
			+"		<div class='ac-alert-caution'>"
			+"			<div>									"
			+"				<p>장비 손상을 방지하기 위하여, 비행제어 컴퓨터(FLCC) 장탈시 배선 하네스가 손상되지 않도록 주의하라.</p>"
			+"			</div>"
			+"		</div>"
			+"		<p class='step1'>1.(A)9개의 전기 커넥터를 분리한다.</p>"
			+"		<p class='step1'>1.(A)비행 제어 컴푸터에서 2개의 장착대 잠금쇠를 푼다.비행 제어 컴푸터에서 2개의 장착대 잠금쇠를 푼다.<br>비행 제어 컴푸터에서 2개의 장착대 잠금쇠를 푼다.</p>"
			+"		<div class='ac-alert-note'>"
			+"			<div>									"
			+"				<p>개방된 모든 전기 커넥터에 보호 장치를 장착하라.</p>"
			+"			</div>"
			+"		</div>"
			+"		<p class='step1'>1.(A)9개의 전기 커넥터를 분리한다.</p>"
			+"		<p class='step1'>1.(A)비행 제어 컴푸터에서 2개의 장착대 잠금쇠를 푼다.</p>"
			+"		<div class='ac-alert-warning'>"
			+"			<div>									"
			+"				<p class='alert-step1'>● 인명 상해를 방지하기 위하여, 비행 제어 컴퓨터 장탈시 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라인명 상해를 방지하기 위하여, 비행 제어 컴퓨터 장탈시 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라 주의하라인명 상해를 방지하기 위하여, 비행 제어 컴퓨터 장탈시 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라.</p>"
			+"				<p class='alert-step2'>● 인명 상해를 방지하기 위하여, 비행 제어 컴퓨터 장탈시 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라장비 구조물에 손이 접근하지 않도록 주의하라.</p>"
			+"				<p class='alert-step3'>● 인명 상해를 방지하기 위하여, 비행 제어 컴퓨터 장탈시 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라.</p>"
			+"				<p class='alert-para1'>인명 상해를 방지하기 위하여, 비행 제어 컴퓨터 장탈시 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라장탈시 항공기 및 장비 구조물에 손이 접근하지 않도록 주의하라..</p>"
			+"			</div>"
			+"		</div>"
			+"		<div class='ac-object'>"
			+"			<img src='"+projectName+"web/image/common/img1.jpg' width='50%' height='400px' alt='3.1.1이미지' />"
			+"			<p>3.1.1 비행 제어 컴퓨터(FLCC) 장탈</p>						"	
			+"		</div>"
			+"		<div class='ac-object'>"
			+"			<object width='640' height='400px'>" 
			+"				<embed src='bookmark.swf'>" 
			+"			</object>"
			+"		</div>"
			+"		<div class='ac-object'>"
			+"			<video width='640' height='' controls>                                                        "
			+"				<source src='"+projectName+"web/image/ietm/mp/jg_1.mp4'>                                                                                                        "
			+"			</video>                                                                                                                                    "
			+"		</div>"
			+"		<div class='ac-object'>"
			+"			<audio width='' height='' controls>                                                        "
			+"				<source src='horse.mp3' type='audio/mpeg'>                                                                                                       "
			+"			</audio>                                                                                                                                    "
			+"		</div>"
			+"</div>";
	
	
	public static StringBuffer ipbHtml = new StringBuffer().append(""
			+"<div id='ipb_main' class='main_container'>"
			+"<div id='' class='ipb_cont'>"
			+"	<div id='' class='cont_l'>"
			+"		<object><embed id='11' src='"+projectName+"web/ietmdata/image/1T-50A-4-27-001-01.swf' style='width:80%;'></object>"
			+"		<p>그림49. 분해</p>"
			+"		<p>[1] [2]</p>"
			+"	</div>"
			+"	<div class='cont_r' id='ipb_div_h'>"
			+"		<table class='ipb_table'>"
			+"		<thead>"
			+"			<tr>"
			+"				<th scope='col' width='3%'>IPB-Code</th>"
			+"				<th scope='col' width='3%'>LCN</th>"
			+"				<th scope='col' width='3%'>계통-그림번호/시트번호</th>"
			+"				<th scope='col' width='2.5%'>품목번호</th>"
			+"				<th scope='col' width='5%'>부품번호</th>"
			+"				<th scope='col' width='3.5%'>국가재고 번호(NSN)</th>"
			+"				<th scope='col' width='3%'>생산자 부호 (Cage)</th>"
			+"				<th scope='col' width='2.5%'>조립체 수준</th>"
			+"				<th scope='col' width='25%'>설명(NOMENCLATURE)</th>"
			+"				<th scope='col' width='3%'>단위당 구성수량(UPA)</th>"
			+"				<th scope='col' width='3%'>Retrofit</th>"
			+"				<th scope='col' width='4%'>사용성 부호(UOC)</th>"
			+"				<th scope='col' width='3%'>근원정비 복구성부호<br>(SMR)</th>"
			+"				<th scope='col' width='3%'>참조 지시번호<br>(RDN)</th>"
			+"				<th scope='col' width='3%'>작업단위부호<br>(WUC)</th>"
			+"				<th scope='col' width='3%'>부품제원</th>"
			+"				<th scope='col' width='3%'>참고자료</th>"
			+"				<th scope='col' width='3%'>참고 교범번호</th>"
			+"				<th scope='col' width='3%'>KAI 규격관리 도면</th>"
			+"				<th scope='col' width='5%'>SSSN</th>"
			+"			</tr>"
			+"		</thead>"
			+"		<tbody>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-1</td><td><a href='#'>0</a></td><td></td><td></td><td></td><td>2</td><td><a href='#' class='left'>압축<br>(COMPRESSION)</a></td><td>REF</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-301</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'>우측 콘솔 장비 장착도, 전방석 <br>(EQUIPMENT INSTL - RH CONSOLE, FWD CREW STA)</a></td><td>REF</td><td>R</td><td>BY</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'>우측 콘솔 장비 장착도, 전방석 <br>(EQUIPMENT INSTL - RH CONSOLE, FWD CREW STA)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'>APS 브래킷 장착도, 중앙 동체<br>(BRACKET INSTL-APS, CENTER FUSELAGE))</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>			"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'>브래킷 장착도, 전방 ECS 블리드 에어<br>(BRACKET INSTL, ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>			"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-3</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-2</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"			<tr>"
			+"				<td></td><td></td><td>21-1</td><td><a href='#'>1</a></td><td><a href='#'>85F2300-311</a></td><td></td><td>3340F</td><td>3</td><td><a href='#' class='left'> 덕트 장착도, 전방 ECS 블리드 에어<br>(DUCT INSTL,ECS BLEED AIR-FWD)</a></td><td>REF</td><td>R</td><td>BZ</td><td>XC</td><td></td><td>41Z00</td><td></td><td></td><td></td><td></td><td>21-11-00</td>"
			+"			</tr>"
			+"		</tbody>"
			+"		</table>"
			+"	</div>"
			+"</div>"
			+"<div id='' class='control'>"
			+"	<a href='#'>상위</a>"
			+"	<a href='#'>도해도</a>"
			+"	<a href='#'>테이블</a>"
			+"	<a href='#'>전체보기</a>"
			+"</div>"
			+"</div>");

	
	public static StringBuffer fiDIHtml = new StringBuffer().append(""
			+ "<div class='fi-content'>\n"
			+ "	<div class='fi-topic'>\n"
			+ "		<a href='#' class='ac-arrow-open' id='ac-title_1' onclick='javascript:divOpen(1);'>&nbsp;</a>조종사 탐지결함\n"
			+ "	</div>\n"
			+ "	<div class='fi-task' id='ac-div_1'>\n"
			//+ "		<div class='fi-sub-topic'>조종사 탐지결함</div>\n"
			+ "		<table class='in_table'>\n"
			+ "			<colgroup>\n"
			+ "			<col width='40%'/>\n"
			+ "			<col />\n"
			+ "			</colgroup>\n"
			+ "			<tbody>\n"
			+ "				<tr>\n"
			+ "					<td rowspan='2'  class='part1'>\n"
			+ "						<img src='image/ietm/fi/img_fi.jpg' width='' height='' alt='' />\n"
			+ "					</td>\n"
			+ "					<td class='part2'>\n"
			+ "						<div class='col'>\n"
			+ "							<strong>전방 DC 전원 패널:</strong>\n"
			+ "							<p>1. Flcs Battpwr Cont Chan A</p>\n"
			+ "							<p>2. Flcs Battpwr Cont Chan B</p>\n"
			+ "							<p>3. Flcs Battpwr Cont Chan C</p>\n"
			+ "							<p>4. Flt Cont Power Branch A</p>\n"
			+ "							<p>5. Flt Cont Power Branch B</p>\n"
			+ "							<p>6. Flt Cont Power Branch C</p>\n"
			+ "						</div>\n"
			+ "						<div class='col'>\n"
			+ "							<strong>후방 장비실 AC/DC 전원 패널:</strong>\n"
			+ "							<p>7. Flcs Battpwr Cont Chan A</p>\n"
			+ "							<p>8. Flcs Battpwr Cont Chan B</p>\n"
			+ "							<p>9. Flcs Battpwr Cont Chan C</p>\n"
			+ "						</div>\n"
			+ "						<div class='col'>\n"
			+ "							<strong>배터리 충전디 조정 유니트:</strong>\n"
			+ "							<p>7. Flcs Battpwr Cont Chan A</p>\n"
			+ "							<p>8. Flcs Battpwr Cont Chan B</p>\n"
			+ "							<p>9. Flcs Battpwr Cont Chan C</p>\n"
			+ "						</div>\n"
			+ "					</td>\n"
			+ "				</tr>\n"
			+ "				<tr>\n"
			+ "					<td class='part3'>\n"
			+ "						<div class='col'>\n"
			+ "							<strong>전방 DC 전원 패널:</strong>\n"
			+ "							<p>해당사항 없음<span>00</span></p>\n"
			+ "						</div>\n"
			+ "					</td>\n"
			+ "				</tr>\n"
			+ "			</tbody>\n"
			+ "		</table>\n"
			+ "		<div id='' class='fi-link'>\n"
			+ "			<a href='#'>(1) 플래퍼론</a>\n"
			+ "			<a href='#'>(2) FLCS 주의등/경고등의 점등 없이 플래퍼론이 항공기에 과도한 진동을 초래함.</a>\n"
			+ "			<a href='#'>(3) 수록되지 않은 플래퍼론 조종사 탐지 결함.</a>\n"
			+ "		</div>\n"
			+ "	</div>\n"
			+"</div>");
	
	
	public static StringBuffer fiLRHtml = new StringBuffer().append(""
			+ "<div class='fi-content'>\n"
			+ "	<div class='fi-topic'>\n"
			+ "		<a href='#' class='ac-arrow-open' id='ac-title_1' onclick='javascript:divOpen(1);'>&nbsp;</a>조종사 탐지결함\n"
			+ "	</div>\n"
			+ "	<div class='fi-task' id='ac-div_1'>\n"
//			//+ "		<div class='fi-sub-topic'>조종사 탐지결함</div>\n"
			+ "		<ul class='fi-input-item'>\n"
			+ "			<li>\n"
			+ "				<span class='num'>1.</span> \n"
			+ "				<ul>\n"
			+ "					<li><span></span><p class='lin-underline'>플래퍼론</p></li>\n"
			+ "					<li><span>AA</span><p>플래퍼론; 시헌번호</p></li>\n"
			+ "					<li><span>AD</span><p>플래퍼론; 시헌번호; FCS 043, 044, 057, 058</p></li>\n"
			+ "					<li><span>AE</span><p>플래퍼론; 시헌번호; FCS 127</p></li>\n"
			+ "					<li><span>AF</span><p>플래퍼론; 시헌번호; FCS 129</p></li>\n"
			+ "				</ul>\n"
			+ "			</li>\n"
			+ "			<li>\n"
			+ "				<span class='num'>2.</span> \n"
			+ "				<ul>\n"
			+ "					<li>\n"
			+ "						<span>XD</span>\n"
			+ "						<p class='lin-underline'>\n"
			+ "							FLCS 주의등/경고등의 점등 없이 플래퍼론이 항공기에 과도한 진동을 초래함FLCS 주의등/경고등의 점등 없이 플래퍼론이 항공기에 과도한 진동을 초래함FLCS 주의등/경고등의 점등 없이 플래퍼론이 항공기에 과도한 진동을 초래\n"
			+ "						</p>\n"
			+ "					</li>\n"
			+ "				</ul>\n"
			+ "			</li>\n"
			+ "			<li>\n"
			+ "				<span class='num'>3.</span> \n"
			+ "				<ul>\n"
			+ "					<li>\n"
			+ "						<span></span>\n"
			+ "						<p class='lin-underline'>수록되지 않은 플래퍼론 조종사 탐지 결함</p>\n"
			+ "					</li>\n"
			+ "					<li>\n"
			+ "						<span>00</span>\n"
			+ "						<p>수록되지 않은 플래퍼론 조종사 탐지 결함 증상을 상세히 기술</p></li>\n"
			+ "				</ul>\n"
			+ "			</li>\n"
			+ "		</ul>\n"
			+ "	</div>\n"
			+ "	<div class='fi-topic'>\n"
			+ "		<a href='#' class='ac-arrow-open' id='ac-title_1' onclick='javascript:divOpen(2);'>&nbsp;</a>작동점검 탐지결함\n"
			+ "	</div>\n"
			+ "	<div class='fi-task' id='ac-div_2'>\n"
			//+ "		<div class='fi-sub-topic'>작동점검 탐지결함</div>\n"
			+ "		<ul class='fi-input-item'>\n"
			+ "			<li>\n"
			+ "				<span class='num'>1.</span> \n"
			+ "				<ul>\n"
			+ "					<li><span></span><p class='lin-underline'>플래퍼론</p></li>\n"
			+ "					<li><span>AA</span><p>플래퍼론; 시헌번호</p></li>\n"
			+ "					<li><span>AD</span><p>플래퍼론; 시헌번호; FCS 043, 044, 057, 058</p></li>\n"
			+ "					<li><span>AE</span><p>플래퍼론; 시헌번호; FCS 127</p></li>\n"
			+ "					<li><span>AF</span><p>플래퍼론; 시헌번호; FCS 129</p></li>\n"
			+ "				</ul>\n"
			+ "			</li>\n"
			+ "			<li>\n"
			+ "				<span class='num'>2.</span> \n"
			+ "				<ul>\n"
			+ "					<li>\n"
			+ "						<span>XD</span>\n"
			+ "						<p class='lin-underline'>FLCS 주의등/경고등의 점등 없이 플래퍼론이 항공기에 과도한 진동을 초래함</p>\n"
			+ "					</li>\n"
			+ "				</ul>\n"
			+ "			</li>\n"
			+ "			<li>\n"
			+ "				<span class='num'>3.</span> \n"
			+ "				<ul>\n"
			+ "					<li>\n"
			+ "						<span></span>\n"
			+ "						<p class='lin-underline'>수록되지 않은 플래퍼론 조종사 탐지 결함</p>\n"
			+ "					</li>\n"
			+ "					<li>\n"
			+ "						<span>00</span>\n"
			+ "						<p>수록되지 않은 플래퍼론 조종사 탐지 결함 증상을 상세히 기술</p>\n"
			+ "					</li>\n"
			+ "				</ul>\n"
			+ "			</li>\n"
			+ "		</ul>\n"
			+ "	</div><!-- fi-task end -->\n"
			+"</div>");
	
	
	public static StringBuffer fi3Html = new StringBuffer().append(""
			+ "<div class='fi-content'>\n"
			+ "	<div class='fi-title-algorithm'>\n"
			+ "		<span class='balloon01'>결함코드</span>\n"
			+ "		<span class='balloon02'>고장탐구 점검 절차</span>\n"
			+ "		<span class='balloon03'>결함 수정 조치</span>\n"
			+ "	</div>\n"
			+ "</div>");
	
	
	public static StringBuffer fi4Html = new StringBuffer().append(""
			+ "<div class='fi-content'>\n"
			+ "	<div class='fi-topic'>\n"
			+ "		<a href='#' class='ac-arrow-open' id='ac-title_1' onclick='javascript:divOpen(1);'>&nbsp;</a>로그 북 리포트\n"
			+ "	</div>\n"
			+ "	<div class='fi-task' id='ac-div_2'>\n"
			+ "		<ul class='in-link'>\n"
			+ "			<li><a href='#'>AA-AO</a></li>\n"
			+ "			<li><a href='#'>AE</a></li>\n"
			+ "			<li><a href='#'>AF</a></li>\n"
			+ "			<li><a href='#'>XD</a></li>\n"
			+ "			<li><a href='#'>ZD</a></li>\n"
			+ "		</ul>\n"
			+ "	</div>\n"
			+ "</div>");


}
