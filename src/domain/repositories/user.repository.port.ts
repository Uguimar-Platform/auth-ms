import { UserModel } from "../models/user.model";

export interface UserRepositoryPort{
    createUser(user: UserModel): Promise<UserModel>;
}