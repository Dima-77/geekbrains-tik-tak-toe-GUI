"c:\Program Files\Java\jdk1.8.0_92\bin\javac" MainClass.java
md jar
"c:\Program Files\Java\jdk1.8.0_92\bin\jar" cvfe jar\tic-tac-toe.jar MainClass *.class
md Class
move /Y *.class Class\
echo ������ ��⭨祪 ��� ����᪠ � ����� Class
echo chcp > Class\runJava.bat
echo "c:\Program Files\Java\jdk1.8.0_92\bin\java" MainClass >> Class\runJava.bat
echo pause >> Class\runJava.bat
pause