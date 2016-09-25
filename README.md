# UCSYLang
UCSY (Unified Computing Secrets for You)

Master ေက်ာင္းသားဘ၀တံုးက ဘာနဲ. Thesis တင္သလဲဆုိေတာ့
OOP ကို support လုပ္ၿပီး ေနာက္ထပ္ Design Pattern ေတြအတြက္ ေထာက္ပံ့ထားတဲ့
Abstraction ေတြ Construct ေတြပါတဲ့ Programming Language တစ္ခုေပါ့

၂၀၀၉ မွာၿပီးပါတယ္။ သံုးႏွစ္ေလာက္ေတာ့ၾကာတယ္ထင္တယ္။

သူ.အတြက္ Compiler ေရးရတယ္ Java နဲ.ေပါ့
(ဆရာမဂ်ီးတစ္ေယာက္က ေၿပာဖူးတယ္ Java ယူသံုးတယ္တဲ့ အံ့ေရာ
ဟီး Compiler ကုိ programming language တစ္ခုနဲ. implement မလုပ္ပဲဘာနဲ.ေရးခ်င္လဲမသိ :P)
ေနာက္ IDE သူ.ကိုလဲ Java နဲ.ေရးရတယ္
ေနာက္ဆံုးတစ္ခုက Virtual Machine သူ.ကို C++ နဲ.ေရးတယ္

ဘာေကာင္းလဲဗ်ာ ဆုိေတာ့ ဘာေကာင္းတယ္ထင္သလဲ
Java အတြက္က bytecode .class ဖုိင္ထြက္တယ္
Java bytecode က instruction 200 ေက်ာ္ရိွတယ္
ကြ်န္ေတာ့္ language က 110 ေက်ာ္ပဲရိွတယ္
ခ်ံဳ.ထားတယ္။ ပိုေကာင္းတယ္လုိ.ဆုိခ်င္တာမဟုတ္ဘူး VM implementation ကိုေသးေအာင္ လုပ္ထားတာ။

အဲ့ေတာ့ Virtual machine ေရးရတာသက္သာတယ္ေပါ့ဗ်ာဆုိခ်င္တာက Java VM (JVM) အတြက္ က C++ code line ၁၀ သိန္းရွိသဗ်
ေမာင္သက္ခုိင္တုိ. မုိက္ခ်က္ က line 5000 ေက်ာ္နဲ.ၿပီးတယ္

Parser Generator ကုိေတာ့ JavaCC ကိုသံုးတယ္။ ဘာလုိ.ဒါေတြသံုးလဲဆုိေတာ့ modern compiler developement မွာအဲ့လိုသံုးတာပိုၿမန္တယ္။ Swift ဆုိ LLVM ဆုိတဲ့ framework ၾကီးကုိသံုးတယ္ သူ.မွာ code generation ကအစပါတယ္။ အဲ့ေတာ့သိပ္မရွုပ္ေတာ့ဘူးေပါ့။

ဒါဆုိဘာလုိ.ခုနက framework ၾကီးေတြမသံုးလဲဆုိေတာ့့ parser generator ေလာက္သံုးတာေတာင္ ငါစကားႏြားရနဲ. မင္းက Java သံုးေရးထားတာ ဘာၿဖစ္တယ္ဆုိၿပီး code တလံုးမွမၾကည့္ပဲ ေၿပာသြားတဲ့သူရိွလုိ. အဲ့အကြက္ၾကိဳသိလုိ.။ ထားပါေလ ဒါေတြက တကယ္လုပ္ၿဖစ္ရင္ေတာ့ ကုိလုပ္မဲ့အလုပ္ပဲဂရုစုိက္ပါ ေဘးကလူေတြ ဂရုစိုက္ေနရင္ မၿပီးေတာ့ဘူး။

အဲ့ေတာ့ဘာၿဖစ္လဲ ေက်ာင္းသားေတြေလ့လာရင္ နားလည္ရလြယ္ေအာင္ေပါ့ အဲလို design ထုတ္ထားတာ
(ေၿပာေသးတယ္ ဆရာမဂ်ီးက မင္းကေတာ့ VM ကုိယ္တုိင္ေရးတယ္ေၿပာတာပဲတဲ့ Third Seminar တံုးကေပါ့ Code ေတာ့ သတၱိနည္းေလေတာ့ဖြင့္မစစ္ရဲရွာဘူး ထားေပေတာ့ စကားခ်ပ္ :P)
Compiler ကေန source file .ucsy ကုိလက္ခံၿပီးေတာ့ compile လုပ္ရင္ .ucode ဆုိတာထြက္ပါတယ္
universal code ေပါ့ (ucsy )သံုးလို. တရားစြဲမယ္ဆုိတဲ့ သူကပါေသး(ထားပါေတာ့ေလ)

Compile လုပ္ဖုိ.က 
http://www.thetkhine.com/project/OOP5.rar

ဒီအေပၚကလင့္ကိုေဒါင္းၿပီး rar ေၿဖလုိက္ပါ

မဟုတ္ရင္ဒီ Github က code ေတြ download လုပ္လဲ ရပါတယ္။

JRE ေတာ့လိုပါတယ္ OOP5 ဆုိတဲ့ folder ထဲမွာ IDE.bat ကို run လုိက္ရင္ IDE တက္လာပါလိမ့္မယ္
အဲဒီမွာ File Open ကေန HelloWorld.ucsy ကုိဖြင့္လုိက္ပါ. Build menu ကေန Compile ဆုိရင္
HelloWorld.ucode ဆုိတာထုတ္ေပးပါလိမ့္မယ္

Command prompt ကေန UVMProject HelloWorld
ဆုိတာကုိသံုးၿပီး run လို.ရပါၿပီ

Bytecode Listing ေတြကို html ဖုိင္နဲ.ၾကည့္ခ်င္ရင္ေတာ့

ucsydh HelloWorld
ဆုိရင္ရပါၿပီ

Language syntax ကေတာ့ C/C++ family ေပမဲ့အကုန္တူတာေတာ့မဟုတ္ဘူးခင္ဗ်။

HelloWorld နဲ.ပဲၿပလုိ. အဲ့ဒါပဲ run လုိ.ရတယ္ထင္ေနဦးမယ္ Object Oriented Programming ကို fully support လုပ္ပါတယ္။ Design pattern ေတြ create လုပ္ႏုိင္တဲ့ Construct အသစ္ေတြပါ ပါေသးတာေပါ့ဗ်ာ။စိတ္၀င္စားရင္ .ucsy ဆုိတဲ့ source code ေတြ လိုက္ဖြင့္ၾကည့္ေပါ့ဗ်ာ။

ေမ့လုိ. ucsy အရွည္က Unified Computing Secret for You လို.ေပးထားတာဗ်။

FB ကေတာ့ post ေတြ update ၿဖစ္သဗ်။Master ေက်ာင္းသားဘ၀တံုးက ဘာနဲ. Thesis တင္သလဲဆုိေတာ့
OOP ကို support လုပ္ၿပီး ေနာက္ထပ္ Design Pattern ေတြအတြက္ ေထာက္ပံ့ထားတဲ့
Abstraction ေတြ Construct ေတြပါတဲ့ Programming Language တစ္ခုေပါ့

၂၀၀၉ မွာၿပီးပါတယ္။ သံုးႏွစ္ေလာက္ေတာ့ၾကာတယ္ထင္တယ္။

သူ.အတြက္ Compiler ေရးရတယ္ Java နဲ.ေပါ့
(ဆရာမဂ်ီးတစ္ေယာက္က ေၿပာဖူးတယ္ Java ယူသံုးတယ္တဲ့ အံ့ေရာ
ဟီး Compiler ကုိ programming language တစ္ခုနဲ. implement မလုပ္ပဲဘာနဲ.ေရးခ်င္လဲမသိ :P)
ေနာက္ IDE သူ.ကိုလဲ Java နဲ.ေရးရတယ္
ေနာက္ဆံုးတစ္ခုက Virtual Machine သူ.ကို C++ နဲ.ေရးတယ္

ဘာေကာင္းလဲဗ်ာ ဆုိေတာ့ ဘာေကာင္းတယ္ထင္သလဲ
Java အတြက္က bytecode .class ဖုိင္ထြက္တယ္
Java bytecode က instruction 200 ေက်ာ္ရိွတယ္
ကြ်န္ေတာ့္ language က 110 ေက်ာ္ပဲရိွတယ္
ခ်ံဳ.ထားတယ္။ ပိုေကာင္းတယ္လုိ.ဆုိခ်င္တာမဟုတ္ဘူး VM implementation ကိုေသးေအာင္ လုပ္ထားတာ။

အဲ့ေတာ့ Virtual machine ေရးရတာသက္သာတယ္ေပါ့ဗ်ာဆုိခ်င္တာက Java VM (JVM) အတြက္ က C++ code line ၁၀ သိန္းရွိသဗ်
ေမာင္သက္ခုိင္တုိ. မုိက္ခ်က္ က line 5000 ေက်ာ္နဲ.ၿပီးတယ္

Parser Generator ကုိေတာ့ JavaCC ကိုသံုးတယ္။ ဘာလုိ.ဒါေတြသံုးလဲဆုိေတာ့ modern compiler developement မွာအဲ့လိုသံုးတာပိုၿမန္တယ္။ Swift ဆုိ LLVM ဆုိတဲ့ framework ၾကီးကုိသံုးတယ္ သူ.မွာ code generation ကအစပါတယ္။ အဲ့ေတာ့သိပ္မရွုပ္ေတာ့ဘူးေပါ့။

ဒါဆုိဘာလုိ.ခုနက framework ၾကီးေတြမသံုးလဲဆုိေတာ့့ parser generator ေလာက္သံုးတာေတာင္ ငါစကားႏြားရနဲ. မင္းက Java သံုးေရးထားတာ ဘာၿဖစ္တယ္ဆုိၿပီး code တလံုးမွမၾကည့္ပဲ ေၿပာသြားတဲ့သူရိွလုိ. အဲ့အကြက္ၾကိဳသိလုိ.။ ထားပါေလ ဒါေတြက တကယ္လုပ္ၿဖစ္ရင္ေတာ့ ကုိလုပ္မဲ့အလုပ္ပဲဂရုစုိက္ပါ ေဘးကလူေတြ ဂရုစိုက္ေနရင္ မၿပီးေတာ့ဘူး။

အဲ့ေတာ့ဘာၿဖစ္လဲ ေက်ာင္းသားေတြေလ့လာရင္ နားလည္ရလြယ္ေအာင္ေပါ့ အဲလို design ထုတ္ထားတာ
(ေၿပာေသးတယ္ ဆရာမဂ်ီးက မင္းကေတာ့ VM ကုိယ္တုိင္ေရးတယ္ေၿပာတာပဲတဲ့ Third Seminar တံုးကေပါ့ Code ေတာ့ သတၱိနည္းေလေတာ့ဖြင့္မစစ္ရဲရွာဘူး ထားေပေတာ့ စကားခ်ပ္ :P)
Compiler ကေန source file .ucsy ကုိလက္ခံၿပီးေတာ့ compile လုပ္ရင္ .ucode ဆုိတာထြက္ပါတယ္
universal code ေပါ့ (ucsy )သံုးလို. တရားစြဲမယ္ဆုိတဲ့ သူကပါေသး(ထားပါေတာ့ေလ)

Compile လုပ္ဖုိ.က
http://www.thetkhine.com/project/OOP5.rar

ဒီအေပၚကလင့္ကိုေဒါင္းၿပီး rar ေၿဖလုိက္ပါ
JRE ေတာ့လိုပါတယ္ OOP5 ဆုိတဲ့ folder ထဲမွာ IDE.bat ကို run လုိက္ရင္ IDE တက္လာပါလိမ့္မယ္
အဲဒီမွာ File Open ကေန HelloWorld.ucsy ကုိဖြင့္လုိက္ပါ. Build menu ကေန Compile ဆုိရင္
HelloWorld.ucode ဆုိတာထုတ္ေပးပါလိမ့္မယ္

Command prompt ကေန UVMProject HelloWorld
ဆုိတာကုိသံုးၿပီး run လို.ရပါၿပီ

Bytecode Listing ေတြကို html ဖုိင္နဲ.ၾကည့္ခ်င္ရင္ေတာ့

ucsydh HelloWorld
ဆုိရင္ရပါၿပီ

Language syntax ကေတာ့ C/C++ family ေပမဲ့အကုန္တူတာေတာ့မဟုတ္ဘူးခင္ဗ်။

HelloWorld နဲ.ပဲၿပလုိ. အဲ့ဒါပဲ run လုိ.ရတယ္ထင္ေနဦးမယ္ Object Oriented Programming ကို fully support လုပ္ပါတယ္။ Design pattern ေတြ create လုပ္ႏုိင္တဲ့ Construct အသစ္ေတြပါ ပါေသးတာေပါ့ဗ်ာ။စိတ္၀င္စားရင္ .ucsy ဆုိတဲ့ source code ေတြ လိုက္ဖြင့္ၾကည့္ေပါ့ဗ်ာ။

ေမ့လုိ. ucsy အရွည္က Unified Computing Secret for You လို.ေပးထားတာဗ်။

Code ေတြက က ငယ္တုန္းက ေရးထားတာဆုိေတာ့ Structured က်က် မလုပ္ႏုိင္ခဲ့ဘူး။ နည္းနည္းေတာ့သည္းခံေပါ့ဗ်ာ။
