import { PaginatedFilterDto } from "src/infrastructure/dto/list-module.filter.dto";
import { UserModel } from "../models/user.model";

export interface UserRepositoryPort {
  createUser(user: UserModel);
  loginUser(email: string, password: string);
  updateUser(id: number, userData: Partial<UserModel>);
  updateUserStatus(id: number, status: number);
  listUserPaginated(filter: PaginatedFilterDto);
}