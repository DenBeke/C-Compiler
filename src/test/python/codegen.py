import unittest
import subprocess
import glob

def compileC(filename, outputfile):
	cmd = "./bin/c2p < " + filename + " > " + outputfile
	subprocess.call(cmd, shell=True)

def runP(filename, outputfile):
	cmd = "./Pmachine/Pmachine " + filename + " > " + outputfile
	subprocess.call(cmd, shell=True)


class TestStringMethods(unittest.TestCase):
	def test_codegen(self):
		test_files = glob.glob("./src/test/input/codegen/*.c")
	  
		for filename in test_files:
			expected_output_file = filename + ".out"
	  
			shortname = filename.split('/')[-1]
			print shortname
	  
			compileC(filename, "./temp/" + shortname + ".p")
			runP("./temp/" + shortname + ".p", "./temp/" + shortname + ".out")
			contents = subprocess.Popen('cat %s' % "./temp/" + shortname + ".out", shell = True, stdout = subprocess.PIPE).communicate()[0]
			expected_contents = subprocess.Popen('cat %s' % expected_output_file, shell = True, stdout = subprocess.PIPE).communicate()[0]
	  	  
		self.assertEqual(contents[:contents.rfind('\n--> Execution time')], expected_contents)
	  

if __name__ == '__main__':
	unittest.main()
