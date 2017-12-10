
import time

tmp_num = 0

def is_devisible_by(num, devider):
	if(num < devider):
		return False
	elif(num == devider):
		return True
	else:
		return is_devisible_by(num - devider, devider)

def is_devisible_by(num, devider):
	if(num < devider):
		return False
	elif(num == devider):
		return True
	else:
		tmp_result = num / devider
		if(tmp_result == int(tmp_result)):
			return True
		else:
			return False

while(True):
	#time.sleep(1)
	tmp_num = tmp_num + 1
	if(is_devisible_by(tmp_num,3) and is_devisible_by(tmp_num,5)):
		print("Fizz Buzz")
	elif(is_devisible_by(tmp_num, 3)):
		print("Fizz")
	elif(is_devisible_by(tmp_num, 5)):
		print("Buzz")
	else:
		print(str(tmp_num))