export interface UserModel {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  password: string;
  address: string;
  roleId: number;
  createdAt?: Date;
  updatedAt?: Date;
  status?: number;
}