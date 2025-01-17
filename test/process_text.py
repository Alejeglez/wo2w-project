import re
from collections import defaultdict
import os

def process_local_file(file_path):
    try:
        processed_content = []
        word_count = defaultdict(int)
        words_set = set()

        with open(file_path, 'r', encoding='utf-8') as file:
            content = file.read()
        
        lines = content.splitlines()
        
        # Procesar línea por línea
        for line in lines:
            processed_content.append(line)
            line = line.lower()
            line = re.sub(r'[^a-z\s]', ' ', line)
            words = line.split()

            for word in words:
                print(word)
                if 3 <= len(word) <= 5 and is_pronounceable(word): 
                    print('entra')
                    word_count[word] += 1
        
        file_paths = []
        for word, count in word_count.items():
            file_key = f'{word}.txt'
            existing_count = 0
            words_set.add(word)
            
            file_path = os.path.join('words', file_key)
            
            if os.path.exists(file_path):
                with open(file_path, 'r', encoding='utf-8') as existing_file:
                    existing_count = int(existing_file.read())
            
            updated_count = existing_count + count
            
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(str(updated_count))
            
            file_paths.append(file_path)

        return file_paths

    except Exception as e:
        print(f'Error processing file: {e}')
        raise e

def is_pronounceable(word):
    vowels = 'aeiou'
    if not any(vowel in word for vowel in vowels):
        return False
    return not re.match(r'(?i)[^aeiou]{3,}', word)

if __name__ == '__main__':
    file_path = 'mock_book.txt'
    process_local_file(file_path)