import random, copy

class Minesweeper():
    def __init__(self):
        '''initialize movelist, minelist, flaglist, and start the game'''
        self.movelist = []
        self.minelist = []
        self.flaglist = []
        #self.coveredlist = []

        #test boards
        # test_board1 = [[' b ', '   ', ' b ', '   ','   '],
        #               ['   ',' b ','   ','   ','   '],
        #               ['   ',' b ','   ',' b ','   '],
        #               [' b ','   ','   ','   ',' b '],
        #               ['   ','   ',' b ','   ','   ']]
        
        # test_board2 = [['   ', '   ', '   ', '   ','   '],
        #               ['   ','   ','   ','   ','   '],
        #               ['   ',' b ','   ','   ','   '],
        #               [' b ',' b ','   ','   ','   '],
        #               [' b ',' b ',' b ','   ','   ']]
        # self.board = test_board2
        self.play()
    
    def board_create(self, size):
        '''A function to create the user board and the actual game board'''
        #user board
        self.user_board = []
        for i in range(size):
            inner = []
            for j in range(size):
                inner.append(' X ')
                #self.coveredlist.append((i,j))
            self.user_board.append(inner)

        #game board
        self.board = []
        for i in range(size):
            inner = []
            for j in range(size):
                if random.randint(0,5) == 3:
                    inner.append(' b ')
                    self.minelist.append((j,i))
                else:
                    inner.append('   ')
            self.board.append(inner)

    def print(self, board):
        '''A function to print a passed in board'''
        for list in board:
            print(list)
    
    def count_j(self, row, position):
        ''' A helper function to count the number of mines in a given row around a position'''
        count = 0
        if row[position] == ' b ':
            count += 1
        if position == 0:
            if row[position + 1] == ' b ':
                count += 1
        elif position  == len(row) - 1:
            if row[position - 1] == ' b ':
                count += 1
        elif position > 0:
            if row[position - 1] == ' b ':
                count += 1
            if row[position + 1] == ' b ':
                count += 1
        return count


    def mine_number(self):
        ''' Create a board with the numbers around every mine'''
        board_copy = copy.deepcopy(self.board)
        for i in range(len(self.board)):
            for j in range(len(self.board)):
                count = 0
                if self.board[i][j] == ' b ':
                    board_copy[i][j] = ' B '
                    continue
                if i == 0:
                    count += self.count_j(self.board[0], j) + self.count_j(self.board[1], j)
                if i == len(self.board) - 1:
                    count += self.count_j(self.board[len(self.board) - 1], j) + self.count_j(self.board[len(self.board) - 2], j)
                if 0 < i < len(self.board) - 1:
                    count += self.count_j(self.board[i - 1], j) + self.count_j(self.board[i], j) + self.count_j(self.board[i + 1], j)
            
                board_copy[i][j] = ' ' + str(count) + ' '
        
        return board_copy
    
    def check(self, x, y):
        '''
        check if the given coordinates contain a mine, if it is a 0, check all coordinates around it
        '''
        numlist = [' 1 ',' 2 ',' 3 ',' 4 ',' 5 ',' 6 ',' 7 ',' 8 ']
        number = self.mine_number()
        if self.board[y][x] == ' b ':
            return 'lose'
        elif (x, y) in self.movelist:
            return
        elif number[y][x] in numlist:
            self.movelist.append((x, y))
            return
        
        #use recursion to uncover all tiles around a given 0 tile until numbers or edges are found
        else:
            if x == 0 and y == 0:
                #top left corner
                self.movelist.append((x, y))
                self.check(x+1, y), self.check(x+1, y+1), self.check(x, y+1)
                
            elif x == 0 and 0 < y < self.size - 1:
                #left edge
                self.movelist.append((x, y))
                self.check(x, y-1), self.check(x+1, y-1), self.check(x+1, y), self.check(x+1, y+1), self.check(x, y+1)

            elif x == 0 and y == self.size - 1:
                #bottom left corner
                self.movelist.append((x, y))
                self.check(x, y-1), self.check(x+1, y-1), self.check(x+1, y)

            elif y == self.size - 1 and 0 < x < self.size - 1:
                #bottom edge
                self.movelist.append((x, y))
                self.check(x-1, y), self.check(x-1, y-1), self.check(x, y-1), self.check(x+1, y-1), self.check(x+1, y)

            elif y == self.size - 1 and x == self.size - 1:
                #bottom right corner
                self.movelist.append((x, y))
                self.check(x-1, y), self.check(x-1, y-1), self.check(x, y-1)

            elif x == self.size - 1 and 0 < y < self.size - 1:
                #right edge
                self.movelist.append((x, y))
                self.check(x, y+1), self.check(x-1, y+1), self.check(x-1, y), self.check(x-1, y-1), self.check(x, y-1)

            elif x == self.size - 1 and y == 0:
                #top right corner
                self.movelist.append((x, y))
                self.check(x, y+1), self.check(x-1, y+1), self.check(x-1, y)

            elif 0 < x < self.size - 1 and y == 0:
                #top edge
                self.movelist.append((x, y))
                self.check(x-1, y), self.check(x-1, y+1), self.check(x, y+1), self.check(x+1, y+1), self.check(x+1, y)

            else:
                #literally anything else
                self.movelist.append((x, y))
                self.check(x-1, y), self.check(x-1, y+1), self.check(x, y+1), self.check(x+1, y+1), self.check(x+1, y), self.check(x+1, y+1), self.check(x, y+1), self.check(x-1, y+1)
    
    #update the user board by uncovering the move list
    def update_board(self):
        print(self.flaglist, self.minelist)
        for tuple in self.movelist:
            x, y = tuple[0], tuple[1]
            self.user_board[y][x] = self.mine_number()[y][x]
        # for tuple in self.flaglist:
        #     self.user_board[y][x] = ' F '

    def play_again(self):
        ''' A helper function to ask the user if they want to play again'''
        inp = input('Press p to play again, press another input to exit ')
        if inp == 'p':
            self.play()
            return False
        else:
            print('Thanks for playing!')
            return True



    def play(self):
        '''
        This function is called in the __init__ to play the game
        '''
        self.movelist = []
        size = 5 #set a default size

        #loop to show rules or play
        while True:
            print("Welcome to minesweeper, input r to read the rules, otherwise input p to play. ")
            inp = input()
            if inp == 'r':
                print('Here are the rules:')
                print('If you would like to input a flag, add an f to the end of the coordinates as follows: 3,5f')
                print('Each round you will be asked to input a coordinate, and depending on what you hit the following will happen:')
                print('If a number is uncovered, that means that there are x mines in the spaces immediatley surrounding the square')
                print('If there are no mines near the selected square, all squares around will be uncovered until numbers are found')
                print('If a bomb is hit, the game ends')
                print('If all bombs are marked with a flag, you win the game and the board will be uncovered')
                break
            if inp == 'p':
                break

        #get board size
        while True:
            try:
                self.size = int(input('Enter a board size: '))
                break
            except:
                print('Invalid type, please enter an integer')
        self.board_create(self.size)

        #loop to make moves
        while True:
            self.print(self.user_board)
            #get the input coordinate
            while True:
                try:
                    xy = input('Enter an x and y value between 1 and the desired size, in the form x,y. If a flag is desired, enter an f at the end of the coordinates. ')
                    xy = xy.split(',')
                    break
                except:
                    print('Invalid input, try again')
            try:
                #check if it is a flag move, if it is add the move to flag list, toggle flag on that spot
                if xy[-1][-1] == 'f':
                    x,y = int(xy[0]), int(xy[-1][:-1])
                    x-=1 #change coordinates to make them work in the code
                    y=self.size-y

                    if (x,y) in self.flaglist: #remove a given flag
                        self.flaglist.remove((x,y))
                        if (x,y) in self.movelist: #if the tile has already been uncovered
                            self.user_board[y][x] = self.mine_number[y][x]
                        else:
                            self.user_board[y][x] = ' X '
        
                    else:
                        self.flaglist.append((x,y)) #add flag to flaglist
                        self.user_board[y][x] = ' F '
                
                    if set(self.flaglist) == set(self.minelist): #use sets to check becuause order doesn't matter
                        self.print(self.mine_number())
                        print('You win!')
                        if self.play_again(): #play again is true if the user does NOT want to play again
                            return
                    continue

                x,y = int(xy[0]), int(xy[1])
                x-=1 #again changing to work in the code
                y=self.size-y
                self.check(x, y)
            except:
                print('Invalid input, try agian.')
                continue

            #check if the tile was a bomb
            if self.check(x, y) == 'lose':
                print('You hit a mine! Game over.')
                if self.play_again(): #true if user does NOT want to play agian
                    return
                
            else:
                self.update_board()

            

if __name__ == '__main__':
    game = Minesweeper()
