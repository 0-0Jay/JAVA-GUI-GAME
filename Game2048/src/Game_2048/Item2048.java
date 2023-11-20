package Game_2048;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class Item2048 extends JFrame {
	private static final long serialVersionUID = 1L;
	private String userID;
	private String userPW;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
 // 게임 컨트롤러
	JButton[][] box = new JButton[4][4]; // 4*4 패널
	JLabel printscore = new JLabel();
	JLabel gameovertext = new JLabel();
	JLabel itemInfo = new JLabel();
	int[][] num = new int[4][4]; // 레이블에 대입할 실제 값
	int itemNum = 0; // 아이템 번호
	int score = 0;
    ArrayList<Object[]> scoreArray;  // 점수 배열
	final int scoreratio = 1; // 점수 비율
	int countblock = 0;
	JTextArea rank = new JTextArea();
	JTextArea ranknum = new JTextArea();
	JTextArea wja = new JTextArea();
	boolean flag = false;
	DBcon con = new DBcon();

    public Item2048() throws SQLException {
        setTitle("ITEM 2048");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

// <------------------------첫 번째 패널 (로그인 폼) ~ line 112----------------->
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(new Color(206,222,255));

        // 제목
        JLabel titleLabel = new JLabel("ITEM 2048");
        titleLabel.setFont(new Font("나눔고딕", Font.BOLD, 100));

        // ID 입력 칸
        JTextField idField = new JTextField("I D");
        idField.setPreferredSize(new Dimension(200, 30));

        // PW 입력 칸
        JPasswordField pwField = new JPasswordField("password");
        pwField.setPreferredSize(new Dimension(200, 30));
        
        // 로그인 성공/실패 텍스트
        JLabel SF = new JLabel("회원 가입을 누르면 입력한 ID/PW로 회원가입됩니다.");
        SF.setFont(new Font("나눔고딕", Font.ITALIC, 20));

        // Login 버튼
        JButton loginButton = new JButton("로그인");
        loginButton.setPreferredSize(new Dimension(200, 30));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	userID = idField.getText();
            	char[] pwArr = pwField.getPassword();	
            	userPW = "";
            	for (char i : pwArr) userPW += i;
            	try {
					if (con.IDcheck(userID, userPW) == 1) {
						cardLayout.show(cardPanel, "inGame");
					} else if (con.IDcheck(userID, userPW) == 2) {
						SF.setText("로그인 실패! 비밀번호가 틀렸습니다!");
					} else if (con.IDcheck(userID, userPW) == 0){
						SF.setText("로그인 실패! ID를 찾을 수 없습니다. 회원가입을 눌러주세요");
					} else {
						SF.setText("시스템 에러. 코드 변경 필요");
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
        });
        
        // 회원가입 버튼
        JButton signUp = new JButton("회원가입");
        signUp.setPreferredSize(new Dimension(200, 30));
        signUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	userID = idField.getText();
            	char[] pwArr = pwField.getPassword();	
            	userPW = "";
            	for (char i : pwArr) userPW += i;
            	try {
					if (con.createID(userID, userPW) == 1) {
						SF.setText("회원가입 성공! 로그인 가능합니다!");
					} else{
						SF.setText("회원가입 실패! 이미 존재하는 ID 입니다!");
            		} 
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
        });

        // Rank 버튼
        JButton rankButton = new JButton("RANK");
        rankButton.setPreferredSize(new Dimension(200, 30));
        rankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
					scoreArray = con.scoreView();
					cardLayout.show(cardPanel, "Rank");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
        });

        // 레이아웃 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0); // 간격 설정

        loginPanel.add(titleLabel, gbc);
        
        // 아이디 입력 칸
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        loginPanel.add(idField, gbc);
        
        // 비밀번호 입력 칸
        gbc.gridy++;
        loginPanel.add(pwField, gbc);
        
        // 로그인 성공 여부 텍스트
        gbc.gridy++;
        loginPanel.add(SF, gbc);
        
        // 로그인 버튼
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        loginPanel.add(loginButton, gbc);
        
        // 회원가입 버튼
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        loginPanel.add(signUp, gbc);

        // 랭크 버튼
        gbc.gridy++;
        loginPanel.add(rankButton, gbc);
        
        cardPanel.add(loginPanel, "Login");

// <----------------------line 38 ~ 여기까지 로그인 패널------------------------------->        
        
// <------------------------두 번째 패널 (게임 패널) ~ line 327------------------------>
        JPanel gamePanel = new JPanel();
    	gamePanel.setLayout(new BorderLayout());
    		
		//우측 서브패널
		JPanel sub = new JPanel();
		sub.setLayout(new GridLayout(10,1));
		sub.setBackground(Color.lightGray);
		sub.setBackground(new Color(213,240,251));
		
		// title
		JLabel welcome = new JLabel("ITEM 2048");
		welcome.setFont(new Font("나눔고딕", Font.BOLD, 45));
		welcome.setHorizontalAlignment(SwingConstants.CENTER);
		sub.add(welcome);
		
		//점수(글자, 라벨)
		JLabel lscore = new JLabel("          점수          ");
		lscore.setFont(new Font("나눔고딕", Font.BOLD, 35));
		lscore.setHorizontalAlignment(SwingConstants.CENTER);
		sub.add(lscore);

		//점수 (숫자)
		printscore.setHorizontalAlignment(SwingConstants.CENTER);
		printscore.setFont(new Font("나눔고딕", Font.BOLD, 35));
		printscore.setText(Integer.toString(score));
		sub.add(printscore);
	
		// 게임오버 텍스트
		gameovertext.setText("");
		gameovertext.setHorizontalAlignment(SwingConstants.CENTER);
		gameovertext.setFont(new Font("나눔고딕", Font.BOLD, 45));
		gameovertext.setForeground(Color.red);
		sub.add(gameovertext); 
		
		// 아이템
		JLabel itemName = new JLabel("아이템");
		itemName.setFont(new Font("나눔고딕", Font.BOLD, 20));
		itemName.setHorizontalAlignment(SwingConstants.CENTER);
		itemInfo.setFont(new Font("나눔고딕", Font.BOLD, 20));
		itemInfo.setHorizontalAlignment(SwingConstants.CENTER);
		sub.add(itemName);
		sub.add(itemInfo);
		
		// 안내 텍스트
		JLabel notice = new JLabel("매 턴 10% 확률로 아이템이 생성되며, 숫자 클릭 시 사용됩니다.");
		notice.setFont(new Font("나눔고딕", Font.BOLD, 10));
		notice.setHorizontalAlignment(SwingConstants.CENTER);
		sub.add(notice);
		
		// 이동 버튼
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 4, 0, 0));
		JButton left = new JButton("←");
		left.setFont(new Font("나눔고딕", Font.BOLD, 45));
		left.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (flag == true) {
					try {
						activate(1);
						refresh();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
        JButton up = new JButton("↑");
        up.setFont(new Font("나눔고딕", Font.BOLD, 45));
        up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (flag == true) {
					try {
						activate(2);
						refresh();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
        JButton down = new JButton("↓");
        down.setFont(new Font("나눔고딕", Font.BOLD, 45));
        down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (flag == true) {
					try {
						activate(3);
						refresh();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
        JButton right = new JButton("→");
        right.setFont(new Font("나눔고딕", Font.BOLD, 45));
        right.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (flag == true) {
					try {
						activate(4);
						refresh();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

        buttonPanel.add(left);
        buttonPanel.add(up);
        buttonPanel.add(down);
        buttonPanel.add(right);
        sub.add(buttonPanel);
	
		// 시작 / 재시작 버튼
		JButton restart = new JButton("시작 / 재시작");
		restart.setFont(new Font("나눔고딕", Font.BOLD, 45));
		restart.setBackground(new Color(206,222,255));
		restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					flag = true;
					restart();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		sub.add(restart);
		
		
		// 로그아웃
		JButton exitbutton = new JButton("로그아웃");
		exitbutton.setFont(new Font("나눔고딕", Font.BOLD, 45));
		exitbutton.setBackground(new Color(158,184,250));
		exitbutton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        userID = null;
		        userPW = null;
				flag = false;
		    	cardLayout.show(cardPanel, "Login");
		    }
		});
		sub.add(exitbutton);	

		// 메인 프레임 (중앙 4*4 그리드 판)
		JPanel frame = new JPanel();
		frame.setLayout(new GridLayout(4,4,3,3));
		for(int i = 0; i<4; i++)
		{
			for(int j = 0; j<4; j++)
			{
				num[i][j] = 0;
				box[i][j] = new numButton(i, j);
				box[i][j].setLayout(new GridLayout(1,1));
				box[i][j].setBackground(Color.white);		
				box[i][j].setFont(new Font("맑은 고딕", Font.BOLD, 45));
				box[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				box[i][j].setVerticalAlignment(SwingConstants.CENTER);
				box[i][j].setText(" ");
				box[i][j].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							numButton b = (numButton) e.getSource();
							if (flag == true) useItem(b.getRow(), b.getCol());
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				});
				frame.add(box[i][j]);
			}
		}
		gamePanel.add(sub,BorderLayout.EAST);
		gamePanel.add(frame,BorderLayout.CENTER);
		
        cardPanel.add(gamePanel, "inGame");
// <---------------------line 115 ~ 여기까지 게임 패널------------------------------->          
                
// <-----------------------세 번째 패널 (게임 랭킹) ~ line 375------------------------>
        JPanel rankPanel = new JPanel();
        rankPanel.setLayout(new BorderLayout());
        JLabel rankTitle = new JLabel("HIGH SCORE", SwingConstants.CENTER);
        rankTitle.setFont(new Font("나눔고딕", Font.BOLD, 100));
        rankPanel.setBackground(new Color(206,222,255));
        rankPanel.add(rankTitle);
        
        // 테이블에 옮기기
        ArrayList<Object[]> score = con.scoreView();
        String[] columnNames = {"RANK", "ID", "SCORE"};
        DefaultTableModel model = new DefaultTableModel(0, columnNames.length);
        model.setColumnIdentifiers(columnNames);
        for (int i = 1; i <= score.size(); i++) {
        	Object[] row = {i, score.get(i - 1)[0], score.get(i - 1)[1]};
        	model.addRow(row);
        }
        
        // 랭크 테이블
        JTable scoreTable = new JTable(model);
        scoreTable.setFont(new Font("나눔고딕", Font.BOLD, 30));
        scoreTable.setRowHeight(40);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        
        // 뒤로가기 버튼
        JPanel backPanel = new JPanel();
        JButton goBack = new JButton("뒤로가기");
        goBack.setPreferredSize(new Dimension(200, 60));
        goBack.setFont(new Font("나눔고딕", Font.BOLD, 30));
        goBack.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e) {
        		cardLayout.show(cardPanel, "Login");
			}
        });
        backPanel.add(goBack, BorderLayout.CENTER);
        backPanel.setBackground(new Color(206,222,255));
        
        rankPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));
        rankPanel.add(rankTitle, BorderLayout.NORTH);
        rankPanel.add(scrollPane, BorderLayout.CENTER);
        rankPanel.add(backPanel, BorderLayout.SOUTH);
        
        cardPanel.add(rankPanel, "Rank");
// <---------------------line 347 ~ 여기까지 랭킹 패널-------------------------------> 

        // JFrame에 카드 레이아웃 패널 추가
        add(cardPanel);
        // JFrame 보이기
        setVisible(true);
        
    }
	
	public int[] sort(int[] arr)
	{
		for(int i = arr.length - 1; i>0; i-- )
			for(int j = 0; j<i; j++)
				if(arr[j] < arr[j + 1])
				{
					int temp = arr[j];
					arr[j] = arr[j+1];
					arr[j+1] = temp;
				}
		return arr;
	}

	public void randMake() throws SQLException
	{
		Random rd = new Random();
		int count = 0;
		int max = rd.nextInt(2)+1;
		if(countblock >= 12)
			max = 1;
	
		while(true)
		{	
			// 디버깅 =======
			if(isGameover() == true)
			{
				gameovertext.setText("Game Over!");
				flag = false;
				con.scoreUpdate(userID, score);
				break;
			}
			//============
			
			if(count == max)
				break;
			
			int x = rd.nextInt(4);
			int y = rd.nextInt(4);
			
			if(num[x][y] != 0)
				continue;
			
			int temp = rd.nextInt(3); // 0, 1, 2 랜덤 난수 생성
			if(temp == 0) temp = 4;
			else if(temp == 1) temp = 2; // 2는 2/3확률, 4는 1/3확률
			Random rnd = new Random();
			int[] item = {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3};
			int pickItem = item[rnd.nextInt(30)];  // 아이템은 10% 확률로 생성, 각각의 아이템은 1/3 확률로 결정.
			// 아이템 정보를 갱신할 때 기존의 itemInfo를 수정하도록 변경
			if (itemNum == 0 && pickItem != 0) {
			    itemNum = pickItem; // 아이템 확률 조정은 여기서
			    if (itemNum == 1) {  // 랜덤 부수기
			        ImageIcon itemImage = new ImageIcon("C:\\Users\\PC\\Desktop\\자바 과제\\Game2048\\img\\item0.jpg");
			        itemInfo.setIcon(itemImage);
			    }
			    else if (itemNum == 2) {  // ㅡ 부수기
			        ImageIcon itemImage = new ImageIcon("C:\\Users\\PC\\Desktop\\자바 과제\\Game2048\\img\\item1.jpg");
			        itemInfo.setIcon(itemImage);
			    }
			    else if (itemNum == 3) {  // | 부수기
			        ImageIcon itemImage = new ImageIcon("C:\\Users\\PC\\Desktop\\자바 과제\\Game2048\\img\\item2.jpg");
			        itemInfo.setIcon(itemImage);
			    }
			}

			
			num[x][y] = temp;
			box[x][y].setText(Integer.toString(num[x][y]));
			count++;
		}
	}
	
	public boolean emptyblock()
	{
		countblock = 0;
		boolean empty = false;
		
		for(int i = 0; i<4; i++)
		{
			for(int j = 0; j<4; j++)
			{
				if(num[i][j] == 0)
					empty = true;
				else
					countblock++;
			}
		}
		
		return empty;
	}
	
	public boolean isGameover()
	{
		boolean check = false;
		int count = 0;
		
		for(int i = 0; i<3; i++)
			for(int j = 0; j<4; j++)
				if(num[i][j] != num[i+1][j] && num[i][j] != 0)
					count++;
		
		for(int i = 0; i<3; i++)
			for(int j = 0; j<4; j++)
				if(num[j][i] != num[j][i+1] && num[j][i] != 0 && num[j][i+1] != 0)
					count++;

		if(count == 24)
			check = true;
				
		return check;
	}
	
	public void activate(int key) throws SQLException // 블록 합치고 한쪽으로 몰기
	{
//		 37 왼쪽
//		 38 위쪽
//		 39 오른쪽
//		 40 아래쪽
//		
//      좌표값	
//		
//		0,0     1,0     2,0     3,0
//
//		0,1     1,1     2,1     3,1
//
//		0,2     1,2     2,2     3,2
//
//		0,3     1,3     1,3     3,3
		
		boolean br = false;
		
		if(key == 1) // 왼쪽으로
		{
			for(int i = 0; i<4; i++)
			{
				for(int j = 1; j<=3; j++)
				{
					if(num[i][j-1] == 0 && num[i][j]>0)
					{
						num[i][j-1] = num[i][j];
						num[i][j]=0;
						i--;
						br = true;
						break;
					}
				}
				if(br == true)
				{
					br = false;
					continue;
				}
				for(int j = 0; j<3; j++)
				{
					if(num[i][j] == num[i][j+1])
					{
						score += (num[i][j]*scoreratio);
						num[i][j] *= 2;
						num[i][j+1] = 0;
					}
				}
				// ************************** 디버깅
				for(int j = 1; j<=3; j++)
				{
					if(num[i][j-1] == 0 && num[i][j]>0)
					{
						num[i][j-1] = num[i][j];
						num[i][j]=0;
					}
				}
				// **************************** 
			}				
		}
		
		else if(key == 4) // 오른쪽으로
		{
			for(int i = 0; i<4; i++)
			{
				for(int j = 2; j>=0; j--)
				{
					if(num[i][j+1] == 0 && num[i][j]>0)
					{
						num[i][j+1] = num[i][j];
						num[i][j]=0;
						i--;
						br = true;
						break;
					}
				}
				if(br == true)
				{
					br = false;
					continue;
				}
				for(int j = 2; j>=0; j--)
				{
					if(num[i][j+1] == num[i][j])
					{
						score += (num[i][j]*scoreratio);
						num[i][j+1] *= 2;
						num[i][j] = 0;
					}
				}
				for(int j = 2; j>=0; j--)
				{
					if(num[i][j+1] == 0 && num[i][j]>0)
					{
						num[i][j+1] = num[i][j];
						num[i][j]=0;
					}
				}
			}
		}
		
		else if(key == 2) // 위쪽으로
		{
			for(int i = 0; i<4; i++)
			{
				for(int j = 1; j<=3; j++)
				{
					if(num[j-1][i] == 0 && num[j][i]>0)
					{
						num[j-1][i] = num[j][i];
						num[j][i]=0;
						i--;
						br = true;
						break;
					}
				}
				if(br == true)
				{
					br = false;
					continue;
				}
				for(int j = 0; j<3; j++)
				{
					if(num[j][i] == num[j+1][i])
					{
						score += (num[j][i]*scoreratio);
						num[j][i] *= 2;
						num[j+1][i] = 0;
					}
				}
				for(int j = 1; j<=3; j++)
				{
					if(num[j-1][i] == 0 && num[j][i]>0)
					{
						num[j-1][i] = num[j][i];
						num[j][i]=0;
					}
				}
			}				
		}
		
		else if(key == 3) // 아래쪽으로
		{
			for(int i = 0; i<4; i++)
			{
				for(int j = 2; j>=0; j--)
				{
					if(num[j+1][i] == 0 && num[j][i]>0)
					{
						num[j+1][i] = num[j][i];
						num[j][i]=0;
						i--;
						br = true;
						break;
					}
				}
				if(br == true)
				{
					br = false;
					continue;
				}
				for(int j = 2; j>=0; j--)
				{
					if(num[j+1][i] == num[j][i])
					{
						score += (num[j][i]*scoreratio);
						num[j+1][i] *= 2;
						num[j][i] = 0;
					}
				}
				for(int j = 2; j>=0; j--)
				{
					if(num[j+1][i] == 0 && num[j][i]>0)
					{
						num[j+1][i] = num[j][i];
						num[j][i]=0;
					}
				}
			}				
		}
		else // 방향키가 아닌 키면 리턴
			return;
		
		if(emptyblock())
			randMake(); // 랜덤 숫자 생성
	}
	
	public void restart() throws SQLException
	{
		for(int i = 0; i<4; i++)
			for(int j = 0; j<4; j++)
				num[i][j] = 0;
		score = 0;
		gameovertext.setText("");
		countblock = 0;
		randMake();
		refresh();
	}
	
	public void refresh()

	{
		printscore.setText(Integer.toString(score));
		for(int i = 0; i<4; i++)
		{
			for(int j = 0; j<4; j++)
			{
				if(num[i][j] == 0)
				{
					box[i][j].setText(" ");
					box[i][j].setBackground(Color.white);
				}
				else
				{
					box[i][j].setText(Integer.toString(num[i][j]));
					
					if(num[i][j] <= 2)
						box[i][j].setBackground(new Color(243,243,243));
					else if(num[i][j] == 4)
						box[i][j].setBackground(new Color(255,228,185));
					else if(num[i][j] == 8)
						box[i][j].setBackground(new Color(255,208,130));
					else if(num[i][j] == 16)
						box[i][j].setBackground(new Color(255,172,49));
					else if(num[i][j] == 32)
						box[i][j].setBackground(new Color(255,168,0));
					else if(num[i][j] == 64)
						box[i][j].setBackground(new Color(255,66,66));
					else if(num[i][j] <= 256)
						box[i][j].setBackground(new Color(255,233,28));
					else if(num[i][j] <= 2048)
						box[i][j].setBackground(new Color(255,28,28));
					else
					{
						box[i][j].setBackground(Color.black);
						box[i][j].setForeground(Color.white);
					}
				}
			}
		}
	}

	public void useItem(int x, int y) throws SQLException{
		if (itemNum == 1) {  // 랜덤 부수기
			Random rnd = new Random();
			int brk = 0;
			score += num[x][y] * scoreratio;
			num[x][y] = (num[x][y] == 0)? 2 : 0;
			while (brk == 0) {  // 10% 확률로 반복
				brk = rnd.nextInt(10);
				int nx = rnd.nextInt(4);
				int ny = rnd.nextInt(4); 
				score += num[nx][ny] * scoreratio;
				num[nx][ny] = (num[nx][ny] == 0)? 2 : 0;
			}	
		} else if (itemNum == 2) { // ㅡ 부수기
			for (int i = -1; i < 2; i++) {
				if (y + i >= 0 && y + i < 4) {
					score += num[x][y + i] * scoreratio;
					num[x][y + i] = (num[x][y + i] == 0)? 2 : 0;
				}
			}
		} else if (itemNum == 3) { // | 부수기
			for (int i = -1; i < 2; i++) {
				if (x + i >= 0 && x + i < 4) {
					score += num[x + i][y] * scoreratio;
					num[x + i][y] = (num[x + i][y] == 0)? 2 : 0;
				}
			}
		} else {
			return; // 아이템 없으면 리턴
		}
		
		itemNum = 0;
		itemInfo.setIcon(null);
		
		if(emptyblock() && itemNum != 0)
			randMake(); // 랜덤 숫자 생성
		
		refresh();
	}
}
