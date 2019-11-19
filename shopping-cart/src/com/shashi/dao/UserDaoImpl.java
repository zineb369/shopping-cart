package com.shashi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.shashi.beans.CartBean;
import com.shashi.beans.UserBean;
import com.shashi.constants.IUserConstants;
import com.shashi.utility.DBUtil;

public class UserDaoImpl implements UserDao {

	@Override
	public String registerUser(String userName, Long mobileNo, String emailId, String address,int pinCode,
			String password) {
		
		
		UserBean user = new UserBean(userName,mobileNo,emailId,address,pinCode,password);
		
		String status = registerUser(user);
		
		return status;
	}

	@Override
	public String registerUser(UserBean user) {
		
		String status = "User Registration Failed!";
		
		boolean isRegtd = isRegistered(user.getEmailId());
		
		
		if(isRegtd) {
			status = "Email Id Already Registered!";
			return status;
		}
		Connection conn = DBUtil.provideConnection();
		PreparedStatement ps = null;
		if(conn != null) {
			System.out.println("Connected Successfully!");
		}
			
		try {
			
			ps = conn.prepareStatement("insert into "+IUserConstants.TABLE_USER+" values(?,?,?,?,?,?)");
			
			ps.setString(1, user.getUserName());
			ps.setLong(2, user.getMobileNo());
			ps.setString(3, user.getEmailId());
			ps.setString(4, user.getAddress());
			ps.setInt(5, user.getPinCode());
			ps.setString(6, user.getPassword());
			
			int k = ps.executeUpdate();
			
			if(k>0)
				status = "User Registered Successfully!";
			
		} catch (SQLException e) {
			status = "Error: "+e.getMessage();
			e.printStackTrace();
		}
		
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(ps);
		
		return status;
	}

	@Override
	public boolean isRegistered(String emailId) {
		boolean flag = false;
		
		Connection con = DBUtil.provideConnection();
		
		PreparedStatement ps =  null;
		ResultSet rs = null;
		
		try {
			ps = con.prepareStatement("select * from user where email=?");
			
			ps.setString(1, emailId);
			
			rs = ps.executeQuery();
			
			if(rs.next())
				flag = true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);
		
		return flag;
	}

	@Override
	public String addToCart(String emailId, String prodId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String isValidCredential(String emailId, String password) {
		String status = "Login Denied! Incorrect Username or Password";
		
		Connection con = DBUtil.provideConnection();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			ps = con.prepareStatement("select * from user where email=? and password=?");
			
			ps.setString(1, emailId);
			ps.setString(2, password);
			
			rs = ps.executeQuery();
			
			if(rs.next())
				status = "valid";
			
		} catch (SQLException e) {
			status = "Error: "+e.getMessage();
			e.printStackTrace();
		}
		
		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);
		return status;
	}

	@Override
	public UserBean getUserDetails(String emailId, String password) {
		
		UserBean user = null;
		
		Connection con = DBUtil.provideConnection();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = con.prepareStatement("select * from user where emailId=? and password=?");
			rs = ps.executeQuery();
			
			if(rs.next()) {
				user = new UserBean();
				user.setUserName(rs.getString("name"));
				user.setMobileNo(rs.getLong("mobile"));
				user.setEmailId(rs.getString("email"));
				user.setAddress(rs.getString("address"));
				user.setPinCode(rs.getInt("pincode"));
				user.setPassword(rs.getString("password"));
				
				return user;
			}
						
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);
		
		
		return user;
	}

	@Override
	public String addProductToCart(String userId, String prodId) {
		String status= "Failed to Add into Cart";
		
		
		Connection con = DBUtil.provideConnection();
		
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		
		try {
			
			ps = con.prepareStatement("select * from usercart where username=? and prodid=?");
			
			ps.setString(1, userId);
			ps.setString(2, prodId);
			
			rs = ps.executeQuery();
			
			if(rs.next()) {
				
				int prodQuantity = rs.getInt("quantity");
				
				prodQuantity += 1;
				
				ps2 = con.prepareStatement("update usercart set quantity=? where username=? and prodid=?");
				
				ps2.setInt(1, prodQuantity);
				
				ps2.setString(2, userId);
				
				ps2.setString(3, prodId);
				
				int k = ps2.executeUpdate();
				
				if(k>0) 
					status  = "Product Successfully Added to Cart!";
				
			}
			else {
				
				ps2 = con.prepareStatement("insert into usercart values(?,?,?)");
				
				ps2.setString(1, userId);
				
				ps2.setString(2, prodId);
				
				ps2.setInt(3, 1);
				
				int k = ps2.executeUpdate();
				
				if(k>0)
					status = "Product Successfully Added to Cart!";
				
			}
			
		} catch (SQLException e) {
				status = "Error: "+ e.getMessage();
			e.printStackTrace();
		}
		
		
		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);
		DBUtil.closeConnection(ps2);
		
		return status;
	}

	@Override
	public List<CartBean> getAllCartItems(String userId) {
		List<CartBean> items = new ArrayList<CartBean>();
		
		Connection con  = DBUtil.provideConnection();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			ps = con.prepareStatement("select * from usercart where username=?");
			
			ps.setString(1, userId);
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				CartBean cart = new CartBean();
				
				cart.setUserId(rs.getString("username"));
				cart.setProdId(rs.getString("prodid"));
				cart.setQuantity(Integer.parseInt(rs.getString("quantity")));
				
				items.add(cart);
				
			}
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);
		
		
		return items;
	}

}