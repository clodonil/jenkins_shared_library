import os
import unittest
from app import app
  
class BasicTests(unittest.TestCase):   
    # executed prior to each test
    def setUp(self):
        self.app = app.test_client()        
        self.assertEqual(app.debug, False)
 
    # executed after each test
    def tearDown(self):
        pass
 
 
###############
#### tests ####
###############
 
    def test_main_page(self):
        response = self.app.get('/', follow_redirects=True)
        self.assertEqual(response.status_code, 200)
 