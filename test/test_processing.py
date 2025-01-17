import pytest
import os
import process_text

def validate_txt_file(file):
    if not file.endswith('.txt'):
        return False
    with open(file, 'r') as f:
        for line in f:
            parts = line.split()

            if len(parts) != 1:
                return False
            
            count = parts[0]

            if not count.isdigit():
                return False
    return True

@pytest.fixture
def processed_dog_file():

    processed_file_path = "words/dog.txt"

    if not os.path.exists(processed_file_path):
        raise FileNotFoundError(f"{processed_file_path} no se ha creado correctamente.")
    
    yield processed_file_path
    
    if os.path.exists(processed_file_path):
        os.remove(processed_file_path)

@pytest.fixture
def processed_car_file():
    processed_file_path = "words/car.txt"
    
    if not os.path.exists(processed_file_path):
        raise FileNotFoundError(f"{processed_file_path} no se ha creado correctamente.")
    
    yield processed_file_path
    
    if os.path.exists(processed_file_path):
        os.remove(processed_file_path)

def test_validate_car_txt(processed_car_file):
    assert validate_txt_file(processed_car_file) == True

def test_validate_dog_txt(processed_dog_file):
    assert validate_txt_file(processed_dog_file) == True


def test_validate_invalid_txt_file():
    invalid_file = "processed_example.pdf"
    assert validate_txt_file(invalid_file) == False

    invalid_file = "word_example.txt"
    with open(invalid_file, 'w') as f:
        f.write("word\n")
    assert validate_txt_file(invalid_file) == False
    os.remove(invalid_file)
