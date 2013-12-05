#echo "$1" | sed -f ~/bin/candc_tokenizer.sed | ~/bin/candc-1.00/bin/pos --model ~/bin/candc-1.00/models/pos 2> /dev/null | ~/bin/candc-1.00/bin/parser --model ~/bin/candc-1.00/models/parser --super ~/bin/candc-1.00/models/super 2> /dev/null | grep "^[(<]"
echo "$1" | sed -f ~/bin/candc_tokenizer.sed | ~/bin/candc-1.00/bin/candc --models ~/bin/candc-1.00/models 2> /dev/null | grep "^[(<]"

