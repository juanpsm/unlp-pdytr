for i in $(ls -d */); do
  cd $i
  out=`echo $i | sed 's|/$||g'`
  ./run.sh > ../tiempos_"$out"
  cd ..
done